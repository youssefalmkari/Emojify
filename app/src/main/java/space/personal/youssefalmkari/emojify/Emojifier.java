package space.personal.youssefalmkari.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import timber.log.Timber;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();
    private static final double LEFT_EYE_OPEN = 0.5;
    private static final double RIGHT_EYE_OPEN = 0.5;
    private static final double SMILING = 0.15;
    private static final float EMOJI_SCALE_FACTOR = .9f;

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap picture) {

        // Create the face detector, disable tracking and enable classifications
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(picture).build();

        // Detect the faces
        SparseArray<Face> faces = detector.detect(frame);

        // Log the number of faces
        Log.d(LOG_TAG, "detectFaces: number of faces = " + faces.size());

        // Initialize result Bitmap to original picture
        Bitmap resultBitmap = picture;

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0)
        {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Iterate trough faces
            for (int i = 0; i < faces.size(); ++i) {
                Face face = faces.valueAt(i);

                Bitmap emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        whichEmoji(face).getEmoji());

                // Add bitmap to face
                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
            }
        }

        // Release the detector
        detector.release();

        return resultBitmap;
    }

    private static Emoji whichEmoji(Face face)
    {

        // Get classifications
        boolean isLeftEyeOpen, isRightEyeOpen, isSmiling;

        // Left eye
        if (face.getIsLeftEyeOpenProbability() >= LEFT_EYE_OPEN) {
            isLeftEyeOpen = true;
        } else {
            isLeftEyeOpen = false;
        }

        //Right eye
        if (face.getIsRightEyeOpenProbability() >= RIGHT_EYE_OPEN) {
            isRightEyeOpen = true;
        } else {
            isRightEyeOpen = false;
        }

        // Smiling
        if (face.getIsSmilingProbability() >= SMILING) {
            isSmiling = true;
        } else {
            isSmiling = false;
        }

        Emoji emoji = null;
        // Choose correct Emoji
        if (isLeftEyeOpen && isRightEyeOpen && isSmiling){
            emoji = Emoji.SMILE;
        } else if (!isLeftEyeOpen && !isRightEyeOpen && isSmiling) {
            emoji = Emoji.CLOSED_SMILE;
        } else if (isLeftEyeOpen && isRightEyeOpen && !isSmiling) {
            emoji = Emoji.FROWN;
        } else if (!isLeftEyeOpen && !isRightEyeOpen && !isSmiling) {
            emoji = Emoji.CLOSED_FROWN;
        } else if (!isLeftEyeOpen && isRightEyeOpen && isSmiling) {
            emoji = Emoji.LEFT_WINK;
        } else if (!isLeftEyeOpen && isRightEyeOpen && !isSmiling) {
            emoji = Emoji.LEFT_WINK_FROWN;
        } else if (isLeftEyeOpen && !isRightEyeOpen && isSmiling) {
            emoji = Emoji.RIGHT_WINK;
        } else if (isLeftEyeOpen && !isRightEyeOpen && !isSmiling) {
            emoji = Emoji.RIGHT_WINK_FROWN;
        }

        return emoji;
    }

    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face)
    {
        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }
}