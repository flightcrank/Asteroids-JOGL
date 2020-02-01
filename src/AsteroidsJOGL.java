
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.util.ArrayList;

/**
 *
 * @author karma
 */
public class AsteroidsJOGL extends javax.swing.JFrame {

	Renderer rend;
	Ship player;
	ArrayList<Asteroid> asteroids;
	
	public AsteroidsJOGL() {
		 
		asteroids = new ArrayList<>();
		asteroids.add(new Asteroid(Size.BIG));
		asteroids.add(new Asteroid(Size.BIG));
		asteroids.add(new Asteroid(Size.BIG));
		
		player = new Ship();
		rend  = new Renderer(player, asteroids);
		
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                //OpenGL panel
                GLProfile glprofile = GLProfile.get("GL3");
                GLCapabilities glcapabilities = new GLCapabilities(glprofile);
                gLJPanel1 = new GLJPanel(glcapabilities);

                gLJPanel1.addGLEventListener(rend);
                FPSAnimator ani = new FPSAnimator(gLJPanel1, 60);
                ani.start();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setTitle("Asteroids JOGL");

                gLJPanel1.setPreferredSize(new java.awt.Dimension(512, 512));
                gLJPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
                        public void keyPressed(java.awt.event.KeyEvent evt) {
                                gLJPanel1KeyPressed(evt);
                        }
                        public void keyReleased(java.awt.event.KeyEvent evt) {
                                gLJPanel1KeyReleased(evt);
                        }
                });

                javax.swing.GroupLayout gLJPanel1Layout = new javax.swing.GroupLayout(gLJPanel1);
                gLJPanel1.setLayout(gLJPanel1Layout);
                gLJPanel1Layout.setHorizontalGroup(
                        gLJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
                );
                gLJPanel1Layout.setVerticalGroup(
                        gLJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
                );

                getContentPane().add(gLJPanel1, java.awt.BorderLayout.CENTER);

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void gLJPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gLJPanel1KeyPressed
             
		switch (evt.getKeyCode()) {
			
			case KeyEvent.VK_A:
				
				player.rotSpeed = -0.05f;
				break;
			
			case KeyEvent.VK_D:
				
				player.rotSpeed = 0.05f;
				break;
			
			case KeyEvent.VK_W:
				
				player.thrust = true;
				break;
			
			case KeyEvent.VK_SPACE:
				
				for (Bullet bullet : player.bullets) {
					
					if (bullet.visable == false) {
						
						bullet.visable = true;
						bullet.rot = player.rot;
						bullet.posX = player.posX;
						bullet.posY = player.posY;
						bullet.vX = player.dX * 8;
						bullet.vY = player.dY * 8;
						break;
					}
				}
				break;
		}

        }//GEN-LAST:event_gLJPanel1KeyPressed

        private void gLJPanel1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gLJPanel1KeyReleased
 
		switch (evt.getKeyCode()) {
			
			case KeyEvent.VK_A:

				player.rotSpeed = 0.0f;
				break;
			
			case KeyEvent.VK_D:

				player.rotSpeed = 0.0f;
				break;
			
			case KeyEvent.VK_W:
				
				player.thrust = false;
				break;
		}
        }//GEN-LAST:event_gLJPanel1KeyReleased

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(AsteroidsJOGL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(AsteroidsJOGL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(AsteroidsJOGL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(AsteroidsJOGL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>
		//</editor-fold>

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new AsteroidsJOGL().setVisible(true);
			}
		});
	}

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private com.jogamp.opengl.awt.GLJPanel gLJPanel1;
        // End of variables declaration//GEN-END:variables
}
