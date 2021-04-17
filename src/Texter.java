import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.Document;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;

public class Texter extends JFrame implements ActionListener {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    JTextArea area;
    JScrollPane pane;
    String text, frameTitle;
    File activeFile;
    JMenuBar menubar;
    final UndoManager undoManager = new UndoManager();

    Texter() {
        setTitle("Texter - new file");
        frameTitle = getTitle();
        setBounds(0, 0, 800, 600);
        menubar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenuItem newDoc = new JMenuItem("New");
        newDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newDoc.addActionListener(this);

        JMenuItem openDoc = new JMenuItem("Open");
        openDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openDoc.addActionListener(this);

        JMenuItem saveAsDoc = new JMenuItem("Save as");
        saveAsDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        saveAsDoc.addActionListener(this);

        JMenuItem saveDoc = new JMenuItem("Save");
        saveDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveDoc.addActionListener(this);

        JMenuItem printDoc = new JMenuItem("Print");
        printDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printDoc.addActionListener(this);

        JMenuItem exitDoc = new JMenuItem("Exit");
        exitDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        exitDoc.addActionListener(this);

        file.add(newDoc);
        file.add(openDoc);
        file.add(saveDoc);
        file.add(saveAsDoc);
        file.add(printDoc);
        file.add(exitDoc);

        JMenu edit = new JMenu("Edit");

        JMenuItem copyDoc = new JMenuItem("Copy");
        copyDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        copyDoc.addActionListener(this);

        JMenuItem undoDoc = new JMenuItem("Undo");
        undoDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoDoc.addActionListener(this);

        JMenuItem redoDoc = new JMenuItem("Redo");
        redoDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
        redoDoc.addActionListener(this);

        JMenuItem pasteDoc = new JMenuItem("Paste");
        pasteDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        pasteDoc.addActionListener(this);

        JMenuItem cutDoc = new JMenuItem("Cut");
        cutDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        cutDoc.addActionListener(this);

        JMenuItem select_allDoc = new JMenuItem("Select All");
        select_allDoc.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
        select_allDoc.addActionListener(this);

        edit.add(copyDoc);
        edit.add(pasteDoc);
        edit.add(cutDoc);
        edit.add(undoDoc);
        edit.add(redoDoc);
        edit.add(select_allDoc);

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(this);

        help.add(about);

        menubar.add(file);
        menubar.add(edit);
        menubar.add(help);

        setJMenuBar(menubar);

        area = new JTextArea();
        DocumentListener documentListener = new DocumentListener() {
            public void changedUpdate(DocumentEvent documentEvent) {
                // String title = getTitle();
                // setTitle(title + "*");
            }

            public void insertUpdate(DocumentEvent documentEvent) {
                System.out.println("Inserting");
                setTitle(frameTitle + "*");
            }

            public void removeUpdate(DocumentEvent documentEvent) {
                System.out.println("Removing");

                setTitle(frameTitle + "*");
            }
        };

        Document doc = area.getDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent evt) {
                undoManager.addEdit(evt.getEdit());
            }
        });

        area.getDocument().addDocumentListener(documentListener);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("SAN_SERIF", Font.PLAIN, 20));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        pane = new JScrollPane(area);
        pane.setBorder(BorderFactory.createEmptyBorder());
        add(pane, BorderLayout.CENTER);
    } // End of constructor

    void saveAs() {
        JFileChooser saveas = new JFileChooser();
        saveas.setApproveButtonText("Save");
        int action2 = saveas.showOpenDialog(this);
        if (action2 != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File filename = new File(saveas.getSelectedFile() + ".txt");
        BufferedWriter outFile;
        try {
            outFile = new BufferedWriter(new FileWriter(filename));
            area.write(outFile);
            setTitle("Texter - " + saveas.getSelectedFile() + ".txt");
            frameTitle = getTitle();
            activeFile = filename;
        } catch (Exception ignored) {
        }
    }

    void New() {
        Texter t2 = new Texter();
        t2.setVisible(true);
    }

    void open() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt files", "txt");
        chooser.addChoosableFileFilter(restrict);
        int action = chooser.showOpenDialog(this);
        if (action != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            area.read(reader, null);
            setTitle("Texter - " + file.getCanonicalPath());
            frameTitle = getTitle();
            System.out.println("frsm title is " + frameTitle);
            activeFile = file;
            DocumentListener documentListener = new DocumentListener() {
                public void changedUpdate(DocumentEvent documentEvent) {
                    // String title = getTitle();
                    // setTitle(title + "*");
                }

                public void insertUpdate(DocumentEvent documentEvent) {
                    System.out.println("Inserting");
                    setTitle(frameTitle + "*");
                }

                public void removeUpdate(DocumentEvent documentEvent) {
                    System.out.println("Removing");

                    setTitle(frameTitle + "*");
                }
            };
            area.getDocument().addDocumentListener(documentListener);
        } catch (Exception ignored) {
        }
    }

    void save() {
        if (activeFile == null) {
            System.out.println("Inside save if");
            saveAs();
        } else {
            File f = activeFile;
            BufferedWriter outFile;
            try {
                outFile = new BufferedWriter(new FileWriter(f));
                area.write(outFile);
                setTitle("Texter - " + f);
                frameTitle = getTitle();
            } catch (Exception ignored) {
            }
        }
    }

    void printDoc() {
        try {
            area.print();
        } catch (Exception ignored) {
        }
    }

    void copy() {
        // text = ;
        String myString = area.getSelectedText();
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    void paste() {
        Clipboard clipboard2 = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            text = (String) clipboard2.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException | IOException unsupportedFlavorException) {
            unsupportedFlavorException.printStackTrace();
        }
        area.insert(text, area.getCaretPosition());
    }

    void cut() {
        String myString2 = area.getSelectedText();
        StringSelection stringSelection2 = new StringSelection(myString2);
        Clipboard clipboard3 = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard3.setContents(stringSelection2, null);
        area.replaceRange("", area.getSelectionStart(), area.getSelectionEnd());
    }

    void Undo() {
        undoManager.undo();
    }

    void Redo() {
        undoManager.redo();
    }

    void selectAll() {
        area.selectAll();
    }

    void About() {
        new About().setVisible(true);
    }

    void Exit() {
        System.exit(0);
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        System.out.println(cmd);
        switch (cmd) {
            case "New":
                New();
                break;
            case "Open":
                open();
                break;
            case "Save":
                save();
                break;
            case "Save as":
                saveAs();
                break;
            case "Print":
                printDoc();
                break;
            case "Exit":
                Exit();
                break;
            case "Copy":
                copy();
                break;
            case "Paste":
                paste();
                break;
            case "Cut":
                cut();
                break;
            case "Select All":
                selectAll();
                break;
            case "About":
                About();
                break;
            case "Undo":
                Undo();
                break;
            case "Redo":
                Redo();
                break;
        }
    }

    static class About extends JFrame {
        /**
         *
         */
        private static final long serialVersionUID = 1L;

        About() {
            setBounds(600, 200, 700, 600);
            setLayout(null);
            ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("icon\\logo.png"));
            Image i2 = i1.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
            ImageIcon i3 = new ImageIcon(i2);
            JLabel l1 = new JLabel(i3);
            l1.setBounds(150, 40, 400, 80);
            add(l1);

            JLabel l3 = new JLabel(
                    "<html>Made by Tushar Adhatrao <br> Check out my other projects on my github profile:<a href='https://github.com/tusharad/'>Here</a></html>");
            l3.setBounds(150, 130, 500, 300);
            l3.setFont(new Font("SAN_SERIF", Font.PLAIN, 18));
            add(l3);
        }
    }

    public static void main(String[] args) {
        Texter t = new Texter();
        t.setVisible(true);
        t.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
