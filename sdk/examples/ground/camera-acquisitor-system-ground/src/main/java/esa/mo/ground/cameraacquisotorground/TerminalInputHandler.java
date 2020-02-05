/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.ground.cameraacquisotorground;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class TerminalInputHandler extends Thread
{

  private boolean running = true;
  private BufferedReader reader = null;

  public TerminalInputHandler()
  {
    InputStreamReader fileInputStream = new InputStreamReader(System.in);
    reader = new BufferedReader(fileInputStream);
  }

  public void end()
  {
    running = false;
  }

  @Override
  public void run()
  {
    String input = "";

    while (running) {
      try {
        if (reader.ready()) {
          input += reader.readLine();
          System.out.println(input);
        }
      } catch (IOException ex) {
        Logger.getLogger(TerminalInputHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    try {
      reader.close();
    } catch (IOException ex) {
      Logger.getLogger(TerminalInputHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
