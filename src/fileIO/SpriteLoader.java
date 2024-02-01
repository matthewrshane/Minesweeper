package fileIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteLoader {

    private static final int SPRITE_WIDTH = 20;
    private static final int SPRITE_HEIGHT = 20;
    private static final Color[] REMOVE_COLORS = {
            new Color(247, 0, 247),
            new Color(127, 0, 127) };

    public static BufferedImage[] loadSpritesheet(String fileName) {
        File file = new File(fileName);
        BufferedImage image = null;
        
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Ensure the spritesheet size is a multiple of the sprite width and height.
        if(image.getWidth() % SPRITE_WIDTH != 0 || image.getHeight() % SPRITE_HEIGHT != 0) {
            System.err.printf("[Loader] Image %s width and height are not multiples of the sprite width and height!\n", fileName);
            return null;
        }

        int spritesPerRow = (image.getWidth() / SPRITE_WIDTH);
        int spritesPerCol = (image.getHeight() / SPRITE_HEIGHT);

        BufferedImage[] sprites = new BufferedImage[spritesPerRow * spritesPerCol];

        for(int i = 0; i < sprites.length; i++) {
            // Create a new BufferedImage containing this sprite.
            sprites[i] = image.getSubimage((i % spritesPerRow) * SPRITE_WIDTH, (i / spritesPerCol) * SPRITE_HEIGHT, SPRITE_WIDTH, SPRITE_HEIGHT);

            // TODO: Use REMOVE_COLORS[] to create transparency in sprites.
        }

        return sprites;
    }

}
