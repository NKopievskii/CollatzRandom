package PRNG.programm;

import PRNG.lib.SoundEntropy;
import PRNG.lib.RandomCollatz;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MainUI extends JFrame {
    private final SoundEntropy soundEntropy;
    private long[] seeds;
    private long timerStart;
    private long timerEnd;
    private int seedPointer;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JTextArea textArea1;
    private JTextArea textArea2;
    private JButton generateButton;
    private JButton recordButton;
    private JButton stopButton;
    private JButton playButton;
    private JButton openButton;
    private JButton saveBitsButton;
    private JSpinner spinner1;
    private JComboBox comboBox1;
    private JButton getSeedButton;
    private JCheckBox useThreadsCheckBox;
    private JComboBox comboBox2;
    private JButton saveTypeButton;
    private JTextField textField2;
    private JButton testTimeButton;

    public MainUI() {
        super("Pseudorandom number generator");
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        seeds = null;
        SpinnerNumberModel model = new SpinnerNumberModel(10,1,Long.MAX_VALUE, 10);
        spinner1.setModel(model);
        soundEntropy = new SoundEntropy();
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generate();
            }
        });
        saveBitsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave(textArea1);
            }
        });
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOpen();
            }
        });
        recordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    recordButton.setEnabled(false);
                    stopButton.setEnabled(true);
                    playButton.setEnabled(false);
                    openButton.setEnabled(false);
                    soundEntropy.captureAudio();
                } catch (Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundEntropy.stopCapture();
                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                openButton.setEnabled(true);
                seeds = soundEntropy.getSeeds();
                if (seeds!=null) {
                    textField1.setText(String.valueOf(seeds[0]));
                }
            }
        });
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundEntropy.playAudio();
            }
        });
        getSeedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seeds = soundEntropy.getSeeds();
                if (seedPointer > seeds.length)
                    seedPointer=0;
                textField1.setText(String.valueOf(seeds[seedPointer++]));
            }
        });
        comboBox1.addItem("Collatz Random");
        comboBox1.addItem("Random");
        comboBox2.addItem("Integer");
        comboBox2.addItem("Long");
        comboBox2.addItem("Double");
        comboBox2.addItem("Float");
        comboBox2.addItem("Boolean");
        comboBox1.setSelectedIndex(0);
        this.pack();
        testTimeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onTime();
            }
        });
        comboBox1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = comboBox1.getSelectedIndex();
                if (index==1){
                    useThreadsCheckBox.setSelected(false);
                    useThreadsCheckBox.setEnabled(false);
                } else {
                    useThreadsCheckBox.setEnabled(true);
                }

            }
        });
        saveTypeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSave(textArea2);
            }
        });
    }
    private void setAll(boolean set){
        testTimeButton.setEnabled(set);
        generateButton.setEnabled(set);
    }
    private void onTime(){
        //Метод для проверки времени работы выполнения
        long seed;
        //Проверка на наличие начального значения
        if (textField1.getText().equals(""))
            seed = 762718989281214L;
        else
            seed = Long.parseLong(textField1.getText());
        int max = (int) (double) spinner1.getValue();
        setAll(false);
        //Выполнение в нескольких потоках или одном
        if (useThreadsCheckBox.isSelected()){
            threads(max, seed);
        } else  {
            timerStart = System.currentTimeMillis();
            nonThreads(max, seed);
            timerEnd = System.currentTimeMillis();
        }
        setAll(true);
        textField2.setText(String.valueOf(timerEnd - timerStart));
    }
    private void nonThreads(int max, long seed) {
        //Генерация значений в одном потоке
        Random random = new Random(seed);
        RandomCollatz collatz = new RandomCollatz(seed);
        switch (comboBox1.getSelectedIndex()) {
            case 0 -> {
                for (int i = 0; i < max; i++) {
                    collatz.nextLong();
                }
            }
            case 1 -> {
                for (int i = 0; i < max; i++) {
                    random.nextLong();
                }
            }
        }
    }

    private void threads(int max, long seed) {
        //Генерация значений в нескольких потоках
        RandomCollatz collatz = new RandomCollatz(seed);
        Runnable task = null;
        int amount = max / 4;
        task = () -> {
            for (int i = 0; i < amount; i++) {
                collatz.nextLong();
            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        Thread thread3 = new Thread(task);
        Thread thread4 = new Thread(task);
        timerStart = System.currentTimeMillis();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException exception){
            exception.printStackTrace();
        }
        timerEnd = System.currentTimeMillis();
    }
    private void onSave (JTextArea textArea){
        //Сохранение созданых значений
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Saving file...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        File file = new File("untitled.txt");
        fileChooser.setCurrentDirectory(file);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Text file", "txt");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile(), false)) {
                writer.write(textArea.getText());
                writer.flush();
                writer.close();
                JOptionPane.showMessageDialog(this,
                        "The '" + fileChooser.getSelectedFile() + "' file is saved");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    private boolean onOpen() {
        //Открытие файла формата .wav
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Selecting a file...");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Waveform Audio File", "wav");
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
             if (soundEntropy.openFile(fileChooser.getSelectedFile())) {
                 seeds = soundEntropy.getSeeds();
                 textField1.setText(String.valueOf(seeds[0]));
                 return true;
             }
        }
        return false;
    }
    private synchronized void printOut(int typeIndex, long result){
        //Синхронизированный вывод значений
        switch (typeIndex) {
            case 0 -> {
                int num = (int) result;
                textArea1.append(String.format("%1$32s", Integer.toBinaryString(num)).replace(" ", "0") + "\n");
                textArea2.append(num + "\n");
            }
            case 1 -> {
                textArea1.append(String.format("%1$64s", Long.toBinaryString(result)).replace(" ", "0") + "\n");
                textArea2.append(result + "\n");
            }
            case 2 -> {
                double num = (double) result ;
                textArea1.append(String.format("%1$64s", Long.toBinaryString(Double.doubleToLongBits(num)).replace(" ", "0") + "\n"));
                textArea2.append(num + "\n");
            }
            case 3 -> {
                float num = (float) result;
                textArea1.append(String.format("%1$32s", Long.toBinaryString( Float.floatToIntBits(num)).replace(" ", "0") + "\n"));
                textArea2.append(num + "\n");
            }
            case 4 -> {
                boolean num;
                String out;
                if ((result >>> (64 - 1))==1) {
                    out = "1";
                    num = true;
                }
                else {
                    out = "0";
                    num = false;
                }
                textArea1.append(out + "\n");
                textArea2.append(num + "\n");
            }
        }
    }
    private void threads(int max, long seed, int typeIndex){
        //Выполнение с использованием потоков и вывод
        RandomCollatz collatz = new RandomCollatz(seed);
        Runnable task = null;
        int amount = max/4;
                task = () -> {
                    for (int i = 0; i < amount; i++) {
                        long num = collatz.nextLong();
                        printOut(typeIndex, num);
                    }
                };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);
        Thread thread3 = new Thread(task);
        Thread thread4 = new Thread(task);
        timerStart = System.currentTimeMillis();
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        try {
            thread1.join();
            thread2.join();
            thread3.join();
            thread4.join();
        } catch (InterruptedException exception){
            exception.printStackTrace();
        }
        timerEnd = System.currentTimeMillis();
    }
    private void nonThreads(int max, long seed, int typeIndex){
        //Выполнение без использования потоков и вывод
        switch (comboBox1.getSelectedIndex()) {
            case 0 -> {
                RandomCollatz collatz = new RandomCollatz(seed);
                for (int i = 0; i < max; i++) {
                    switch (typeIndex) {
                        case 0 -> {
                            int num = collatz.nextInt();
                            textArea1.append(String.format("%1$32s", Integer.toBinaryString(num)).replace(" ", "0") + "\n");
                            textArea2.append(num + "\n");
                        }
                        case 1 -> {
                            long num = collatz.nextLong();
                            textArea1.append(String.format("%1$64s", Long.toBinaryString(num)).replace(" ", "0") + "\n");
                            textArea2.append(num + "\n");
                        }
                        case 2 -> {
                            double num = collatz.nextDouble();
                            textArea1.append(String.format("%1$64s", Long.toBinaryString(Double.doubleToLongBits(num)).replace(" ", "0") + "\n"));
                            textArea2.append(num + "\n");
                        }
                        case 3 -> {
                            float num = collatz.nextFloat();
                            textArea1.append(String.format("%1$32s", Long.toBinaryString( Float.floatToIntBits(num)).replace(" ", "0") + "\n"));
                            textArea2.append(num + "\n");
                        }
                        case 4 -> {
                            boolean num = collatz.nextBoolean();
                            String out;
                            if (num)
                                out = "1";
                            else
                                out = "0";
                            textArea1.append(out + "\n");
                            textArea2.append(num + "\n");
                        }
                    }
                }
            }
            case 1 -> {
                Random random = new Random(seed);
                for (int i = 0; i < max; i++) {
                    switch (typeIndex) {
                        case 0 -> {
                            int num = random.nextInt();
                            textArea1.append(String.format("%1$32s", Integer.toBinaryString(num)).replace(" ", "0") + "\n");
                            textArea2.append(num + "\n");
                        }
                        case 1 -> {
                            long num = random.nextLong();
                            textArea1.append(String.format("%1$64s", Long.toBinaryString(num)).replace(" ", "0") + "\n");
                            textArea2.append(num + "\n");
                        }
                        case 2 -> {
                            double num = random.nextDouble();
                            textArea1.append(String.format("%1$64s", Long.toBinaryString(Double.doubleToLongBits(num)).replace(" ", "0") + "\n"));
                            textArea2.append(num + "\n");
                        }
                        case 3 -> {
                            float num = random.nextFloat();
                            textArea1.append(String.format("%1$32s", Long.toBinaryString( Float.floatToIntBits(num)).replace(" ", "0") + "\n"));
                            textArea2.append(num + "\n");
                        }
                        case 4 -> {
                            boolean num = random.nextBoolean();
                            String out;
                            if (num)
                                out = "1";
                            else
                                out = "0";
                            textArea1.append(out + "\n");
                            textArea2.append(num + "\n");
                        }
                    }
                }
            }
        }
    }
    private void generate(){
        //Генерация значений
        textArea2.setText("");
        textArea1.setText("");
        long seed;
        if (textField1.getText().equals(""))
            seed = 762718989281214L;
        else
            seed = Long.parseLong(textField1.getText());
        int max = (int) (double) spinner1.getValue();

        int typeIndex = comboBox2.getSelectedIndex();
        boolean threads = useThreadsCheckBox.isSelected();
        setAll(false);
        if (threads)
            threads(max, seed, typeIndex);
        else {
            timerStart = System.currentTimeMillis();
            nonThreads(max, seed, typeIndex);
            timerEnd = System.currentTimeMillis();
        }
        setAll(true);
        textField2.setText(String.valueOf(timerEnd - timerStart));
        setAll(true);
    }
    private void onCancel() {
        dispose();
    }
}
