/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Control.InteracaoJava;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Lavinia
 */
public class _Principal {

    public static int interacao = 86900732;

    public static void main(String args[]) {

        InteracaoJava interacaoJava = new InteracaoJava(interacao); // 86900732 maximo
 
        JOptionPane.showMessageDialog(new JPanel(), "Tempo Total Gasto Utilizando Java: "+"\r\n" +"                     "+ interacaoJava.retornaTempo() +" (ms)", "Tempo Gasto", JOptionPane.INFORMATION_MESSAGE);
        
    }
}
