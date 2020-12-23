
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.Music;
import kuusisto.tinysound.TinySound;

/**
 *
 * @author karma
 */

enum Scene {TITLE, GAME, GAME_OVER, PLAY_AGAIN_GO, PLAY_AGAIN_W, WINNER};

public class AsteroidsJOGL extends javax.swing.JFrame {
	
	public Ship player;
	public ArrayList<Asteroid> asteroids;
	public Renderer rend;
	public Sound lazerSFX;
	public Sound shipDeathSFX;
	public Sound astteroidHitSFX;
	public Music rocketSFX;

	public AsteroidsJOGL() {
		
		TinySound.init();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.player = new Ship();
		this.asteroids = new ArrayList<>();
		this.asteroids.add(new Asteroid(Size.BIG));
		this.asteroids.add(new Asteroid(Size.BIG));
		this.asteroids.add(new Asteroid(Size.BIG));
		this.lazerSFX = TinySound.loadSound("lazer2.wav");
		this.shipDeathSFX = TinySound.loadSound("shipdeath.wav");
		this.rocketSFX = TinySound.loadMusic("rocket2.wav");
		this.astteroidHitSFX = TinySound.loadSound("asteroidhit.wav");
		this.rend = new Renderer(player, asteroids, Scene.TITLE, rocketSFX, shipDeathSFX, astteroidHitSFX);
		rocketSFX.setVolume(0.2);
		
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
                setMinimumSize(new java.awt.Dimension(512, 512));
                addWindowListener(new java.awt.event.WindowAdapter() {
                        public void windowClosing(java.awt.event.WindowEvent evt) {
                                test(evt);
                        }
                });

                gLJPanel1.setMinimumSize(new java.awt.Dimension(512, 512));
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
                        .addGap(0, 512, Short.MAX_VALUE)
                );
                gLJPanel1Layout.setVerticalGroup(
                        gLJPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 512, Short.MAX_VALUE)
                );

                getContentPane().add(gLJPanel1, java.awt.BorderLayout.CENTER);

                pack();
        }// </editor-fold>//GEN-END:initComponents

        private void gLJPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gLJPanel1KeyPressed
                
		if (rend.scene == Scene.GAME || rend.scene == Scene.WINNER || rend.scene == Scene.PLAY_AGAIN_W && player.visable == true) {

			switch (evt.getKeyCode()) {

				case KeyEvent.VK_A:

					player.rotSpeed = 0.05f;
					break;

				case KeyEvent.VK_D:

					player.rotSpeed = -0.05f;
					break;

				case KeyEvent.VK_W:

					player.thrust = true;
					rocketSFX.play(true);
					
					break;

				case KeyEvent.VK_SPACE:

					for (Bullet bullet : player.bullets) {

						if (bullet.visable == false && player.visable == true) {
							
							lazerSFX.play(0.1);
							bullet.visable = true;
							bullet.spawnTime = System.currentTimeMillis();
							bullet.sprite.rot = player.sprite.rot;
							bullet.sprite.position[0] = player.sprite.position[0];
							bullet.sprite.position[1] = player.sprite.position[1];
							bullet.vX = player.dX * 8;
							bullet.vY = player.dY * 8;
							break;
						}
					}

					break;

				default:
					break;
			}
		}	
        }//GEN-LAST:event_gLJPanel1KeyPressed

        private void gLJPanel1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_gLJPanel1KeyReleased
	
		if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {

			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
		
		if (rend.scene == Scene.GAME || rend.scene == Scene.WINNER || rend.scene == Scene.PLAY_AGAIN_W && player.visable == true) {
			
			switch (evt.getKeyCode()) {

				case KeyEvent.VK_A:

					player.rotSpeed = 0.0f;
					break;

				case KeyEvent.VK_D:

					player.rotSpeed = 0.0f;
					break;

				case KeyEvent.VK_W:

					player.thrust = false;
					rocketSFX.stop();
					break;
			}	
		}
		
		if (rend.scene == Scene.TITLE) {
						
			if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
				
				player.reset();
				rend.scene = Scene.GAME;
				player.visable = true;
			}
		}
		
		if (rend.scene == Scene.PLAY_AGAIN_GO || rend.scene == Scene.PLAY_AGAIN_W) {
			
			player.reset();
			player.lives = 3;
			asteroids.clear();
			asteroids.add(new Asteroid(Size.BIG));
			asteroids.add(new Asteroid(Size.BIG));
			asteroids.add(new Asteroid(Size.BIG));
			rend.scene = Scene.TITLE;
		}
		
		if (rend.scene == Scene.GAME_OVER) {
			
			if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
			
				rend.scene = Scene.PLAY_AGAIN_GO;
			}
		}
		
		if (rend.scene == Scene.WINNER) {
			
			if (evt.getKeyCode() == KeyEvent.VK_SPACE) {
			
				rend.scene = Scene.PLAY_AGAIN_W;
			}
		}
        }//GEN-LAST:event_gLJPanel1KeyReleased

        private void test(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_test

		System.out.println("Shuting down tiny sound");
		TinySound.shutdown();
        }//GEN-LAST:event_test

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {	
		
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
