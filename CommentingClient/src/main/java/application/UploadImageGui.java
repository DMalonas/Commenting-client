package application;


import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.ws.rs.client.Client;



public class UploadImageGui extends JFrame{
    private JButton browseButton;
    private JButton uploadButton;
    private JLabel label;
    private String path;
    private String finalPath;
    
    public String getFinalPath() {
		return finalPath;
	}

	public void setFinalPath() {
		this.finalPath = this.path;
	}

	public UploadImageGui(){
    super("Set Picture Into A JLabel Using JFileChooser In Java");
    path = null;
    finalPath = null;
    browseButton = new JButton("Browse");
    uploadButton = new JButton("Upload");
    browseButton.setBounds(250,300,100,40);
    uploadButton.setBounds(350,300,100,40);
    label = new JLabel();
    label.setBounds(10,10,670,250);
    add(browseButton);
    add(uploadButton);
    add(label);
    
    browseButton.addActionListener(new ActionListener() {

        public void actionPerformed(ActionEvent e) {
        
          JFileChooser file = new JFileChooser();
          file.setCurrentDirectory(new File(System.getProperty("user.home")));
          //filter the files
          FileNameExtensionFilter filter = new FileNameExtensionFilter("*.Images", "jpg","gif","png");
          file.addChoosableFileFilter(filter);
          int result = file.showSaveDialog(null);
           //if the user click on save in Jfilechooser
          if(result == JFileChooser.APPROVE_OPTION){
              File selectedFile = file.getSelectedFile();
              path = selectedFile.getAbsolutePath();
              label.setIcon(ResizeImage(path));
          }
           //if the user click on save in Jfilechooser


          else if(result == JFileChooser.CANCEL_OPTION){
              System.out.println("No File Select");
          }
        }
    });
    
    uploadButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
            try {
            setFinalPath();
            setVisible(false);
            } catch (Throwable e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}  
	});
    
    setLayout(null);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setSize(700,400);
    setVisible(true);
    }
     
     public String getPath() {
		return path;
	}

	// Methode to resize imageIcon with the same size of a Jlabel
    public ImageIcon ResizeImage(String ImagePath)
    {
        ImageIcon MyImage = new ImageIcon(ImagePath);
        Image img = MyImage.getImage();
        Image newImg = img.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);
        return image;
    }

}
