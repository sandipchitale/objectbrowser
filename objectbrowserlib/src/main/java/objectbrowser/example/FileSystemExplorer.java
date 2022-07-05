/*
 * %W% %E%
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package objectbrowser.example;

import javax.swing.*;

import groovy.inspect.Inspector;
import objectbrowser.treetable.JTreeTable;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

/**
 * A TreeTable example, showing a JTreeTable, operating on the local file
 * system.
 *
 * @version %I% %G%
 *
 * @author Philip Milne
 */

public class FileSystemExplorer {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try {
                    // Set System L&F
                    UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e) {
                // handle exception
                }
                new FileSystemExplorer();
            }
        });
	}

	public FileSystemExplorer() {
        Inspector inspector = new Inspector(System.getProperties());

        Arrays.asList(inspector.getClassProps()).stream().forEach(i -> {
            System.out.println(i);
        });
        System.out.println();

		JFrame frame = new JFrame("File System Explorer");
        JTreeTable treeTable = new JTreeTable(new FileSystemModel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.exit(0);
			}
		});

		frame.getContentPane().add(new JScrollPane(treeTable));
		frame.pack();
		frame.setVisible(true);
	}
}
