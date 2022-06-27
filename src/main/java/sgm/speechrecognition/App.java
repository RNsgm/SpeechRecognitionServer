package sgm.speechrecognition;

import java.io.IOException;

import sgm.speechrecognition.server.SpeechRecognitionServer;

/**
 * Copyright 2022 Ryabinin Nikolay
 * Licensed under the Apache License, Version 2.0
 * 
 * 27 06 2022
 */
public class App 
{
    public static void main( String[] args )
    {
        try {
            SpeechRecognitionServer server = new SpeechRecognitionServer();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
