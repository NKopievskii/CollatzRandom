package PRNG.lib;

import java.io.*;
import java.util.BitSet;
import javax.sound.sampled.*;
public class SoundEntropy {
    boolean stopCapture = false;
    ByteArrayOutputStream byteArrayOutputStream;
    AudioFormat audioFormat;
    TargetDataLine targetDataLine;
    AudioInputStream audioInputStream;
    SourceDataLine sourceDataLine;
    private long[] seeds;
    private BitSet bitSetSeed;
    public SoundEntropy(){}
    public void captureAudio() {
        try {
            //Установим все для захвата
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            Thread captureThread = new CaptureThread();
            captureThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void stopCapture(){
        stopCapture = true;
    }
    public void playAudio() {
        try {
                byte[] audioData = byteArrayOutputStream.toByteArray();
                InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
                AudioFormat audioFormat = getAudioFormat();
                audioInputStream = new AudioInputStream(
                        byteArrayInputStream,
                        audioFormat,
                        audioData.length / audioFormat.getFrameSize());
                DataLine.Info dataLineInfo = new DataLine.Info(
                        SourceDataLine.class,
                        audioFormat);
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                Thread playThread = new Thread(new PlayThread());
                playThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
    private AudioFormat getAudioFormat() {
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(
                sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }
    public long[] getSeeds() {
        return seeds;
    }
    public boolean openFile(File file){
            try {
                if (file.isFile()) {
                    audioInputStream = AudioSystem.getAudioInputStream(file);
                    audioFormat = audioInputStream.getFormat();
                    byte[] buffer = audioInputStream.readAllBytes();
                    bitSetSeed = BitSet.valueOf(buffer);
                    seeds = bitSetSeed.toLongArray();
                    return true;
                }
            }
            catch (Exception exception){
                exception.printStackTrace();
                return false;
            }
            return false;
    }
    class CaptureThread extends Thread {
        byte[] tempBuffer = new byte[10000];
        public void run() {
            byteArrayOutputStream = new ByteArrayOutputStream();
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                        if (cnt > 60000)
                            stopCapture = true;
                    }
                }
                byteArrayOutputStream.close();
                targetDataLine.close();
                bitSetSeed = BitSet.valueOf(byteArrayOutputStream.toByteArray());
                long[] withZeroes = bitSetSeed.toLongArray();
                int length = withZeroes.length; //100
                long[] withoutZeroes = new long[length];
                int pointer = 0;
                for (long withZeroe : withZeroes) {
                    if (withZeroe != 0 && withZeroe != 1) {
                        withoutZeroes[pointer++] = withZeroe;
                    }
                }
                seeds = new long[length/2]; //50
                System.arraycopy(withoutZeroes, length / 4, seeds, 0, length/2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    class PlayThread extends Thread {
        byte[] tempBuffer = new byte[10000];
        public void run() {
            try {
                int cnt;
                while ((cnt = audioInputStream.
                        read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }
                }
                sourceDataLine.drain();
                sourceDataLine.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}