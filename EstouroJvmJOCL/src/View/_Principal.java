/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View;

import Control.InteracaoOpenCL;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Lavinia
 */
public class _Principal {

    public static int interacao = 86900732;

    public static void main(String args[]) {
 
        InteracaoOpenCL interacaoOpenCl = new InteracaoOpenCL(interacao);
       JOptionPane.showMessageDialog(new JPanel(), "Tempo Total Gasto Utilizando JOCL: "+"\r\n" +"                     "+ interacaoOpenCl.retornaTempo() +" (ms)", "Tempo Gasto", JOptionPane.INFORMATION_MESSAGE);
 
    }
}
