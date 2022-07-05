package objectbrowser;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import objectbrowser.treetable.JTreeTable;

public class ObjectBrowser {
    private ObjectBrowser(Object objectToInspect) {
        JFrame frame = new JFrame("Object Browser (Tree Table)  ");
        try {
            frame.setIconImage(
                ImageIO.read(getClass().getResourceAsStream("/ConsoleIcon.png"))
            );
        } catch (IOException e) {
        }
        JTreeTable treeTable = new JTreeTable(new InspectorModel(objectToInspect));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.getContentPane().add(new JScrollPane(treeTable));
		frame.pack();
		frame.setVisible(true);
	}

    public static void inspect(Object objectToInspect) {
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
                new ObjectBrowser(objectToInspect);
            }
        });
    }

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
                ObjectBrowser.inspect(System.out);
            }
        });
	}
}
