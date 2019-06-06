
public class KickFinder{
    public static void main(String[] args){
        System.out.println("heres a try");

        // Open the wav file specified as the first argument
        WavFile wavFile = WavFile.openWavFile(new File(args[0]));

        // Display information about the wav file
        wavFile.display();

    }
}