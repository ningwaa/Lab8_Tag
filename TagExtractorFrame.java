import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import static java.nio.file.StandardOpenOption.CREATE;

public class TagExtractorFrame extends JFrame
{
    JButton quitButton;
    JButton chooserButton;
    JButton saveButton;
    JTextArea textArea;
    JScrollPane scroller;
    JPanel mainPanel;
    JPanel displayPanel;
    JPanel buttonPanel;
    String name;

    ArrayList<String> soundWords = new ArrayList<>();
    TreeMap<String, Integer> keyWord = new TreeMap<String, Integer>();

    public  TagExtractorFrame()
    {
        setTitle("KEYWORD EXTRACTOR");
        setSize(1000, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        add(mainPanel);

        createDisplayPanel();
        createTitlePanel();


        setVisible(true);
    }

    private void createTitlePanel()
    {
        displayPanel = new JPanel();

        textArea = new JTextArea(30, 40);
        scroller = new JScrollPane(textArea);
        textArea.setFont(new Font("Times New Roman", Font.PLAIN, 30));

        textArea.setEditable(false);
        displayPanel.add(scroller);
        mainPanel.add(displayPanel, BorderLayout.CENTER);
    }

    private void createDisplayPanel()
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));

        chooserButton = new JButton("CHOOSE A FILE");
        chooserButton.setFont(new Font("Times New Roman", Font.BOLD, 25));

        saveButton = new JButton("SAVE TO FILES");
        saveButton.setFont(new Font("Times New Roman", Font.BOLD, 25));

        quitButton = new JButton("QUIT");
        quitButton.setFont(new Font("Times New Roman", Font.BOLD, 25));



        chooserButton.addActionListener(e ->
        {
            readFile();
            for (Map.Entry map : keyWord.entrySet())
            {
                textArea.append(String.format("%-30s%d\n",map.getKey(), map.getValue()));
            }
        });

        saveButton.addActionListener(e ->
        {
            String saveFileName = JOptionPane.showInputDialog("Enter file name");

            File file = new File(System.getProperty("user.dir"));
            Path filePath = Paths.get(file.getPath() + "//src//" + saveFileName + ".txt");

            try
            {
                OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath, CREATE));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                for (Map.Entry map : keyWord.entrySet())
                {
                    writer.write(String.format("%-30s%d\n", map.getKey(), map.getValue()));
                }
                writer.close();
                textArea.append("\nFile Saved To: " + saveFileName + ".txt");

            }
            catch (IOException i)
            {
                i.printStackTrace();
            }
        });

        quitButton.addActionListener(e ->
        {
            JOptionPane pane = new JOptionPane();

            int result = JOptionPane.showConfirmDialog(pane, "Want to exit the window?", "Exit", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION)
            {
                System.exit(0);
            }
            else
            {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
            }
        });


        buttonPanel.add(chooserButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void readStopWords()
    {
        try
        {
            File workingDirectory = new File("src/EnglishStopWords.txt");

            Scanner readFile = new Scanner(workingDirectory);

            while (readFile.hasNextLine())
            {
                soundWords.add(readFile.nextLine());
            }
            readFile.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readFile()
    {
        JFileChooser chooser = new JFileChooser();
        String readLine = "";

        Path target = new File(System.getProperty("user.dir")).toPath();
        target = target.resolve("src");
        chooser.setCurrentDirectory(target.toFile());

        readStopWords();

        try
        {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                target = chooser.getSelectedFile().toPath();
                Scanner inFile = new Scanner(target);

                while (inFile.hasNextLine())
                {
                    readLine = inFile.nextLine().toLowerCase().replaceAll("[^A-Za-z]", " ");
                    name = chooser.getSelectedFile().getName();
                    textArea.setText("File name: " + name + "\n\n\nThe Frequency and the Keywords are:\n\n");
                    String word[] = readLine.split(" ");
                    for (int i = 0; i < word.length; i++)
                    {
                        String current = word[i];

                        if (keywordFrequency(current, keyWord))
                        {

                        }
                        else if (!noiseCheck(current))
                        {
                            gatherKeyWords(current);

                        }
                    }
                }
                inFile.close();
            }
            else
            {
                textArea.setText("File no found, please choose a file.");
            }
        }
        catch (IOException e)
        {
            System.out.println("CAUGHT UP WITH ERROR");
            e.printStackTrace();
        }
    }

    public boolean keywordFrequency
            (String word, TreeMap<String, Integer> keyWords)
    {
        for (Map.Entry map : keyWords.entrySet())
        {
            if (map.getKey().equals(word))
            {
                int frequency = Integer.parseInt(map.getValue().toString()) + 1;
                map.setValue(frequency);

                return true;
            }
        }

        return false;
    }


    public boolean noiseCheck(String word)
    {
        for (String bad : soundWords)
        {
            if (bad.equals(word))
            {
                return true;
            }
        }
        return false;
    }


    public void gatherKeyWords(String word)
    {
        if (word != null && !"".equals(word))
        {
            keyWord.put(word, 1);
        }
    }
}
