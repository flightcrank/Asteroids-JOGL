
# Asteroids JOGL
This is a simple OpenGL program coded in Java using JOGL to recreate the classic
Asteroids game using the OpenGL Graphics API. 

Its currently still under active development, but is working and ready to use if you wish.
Feel free to contact me with any issues or feature requests.

In order to compiles the JOGL libs must be available on the java classpath. The jogl libs
can be found at https://jogamp.org/deployment/v2.3.2/archive/jogamp-all-platforms.7z
It also uses the tinysound lib to play the soundeffects. The licence for which is inside the tinysound jar file.

Compilation instructions on a linux system are as follows... (browse to the src directory)

	$ javac -cp .:path_to_jogl/jar/jogl-all.jar:path_to_jogl/jar/gluegen-rt.jar:tinysound-1.1.1.jar AsteroidsJOGL.java 

that should compile all necessary .java files. (make sure to change "path_to_jogl" with the path to where you unzipped the jogl jar's too) 

to run the program type the command

	$ java -cp .:path_to/jogl/jar/jogl-all.jar:path_to/jogl/jar/gluegen-rt.jar:tinysound-1.1.1.jar AsteroidsJOGL

## Images 
![Imgur](https://i.imgur.com/IIyCII3.gif)
