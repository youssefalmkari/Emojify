package space.personal.youssefalmkari.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getSimpleName();
    private static final double LEFT_EYE_OPEN = 0.5;
    private static final double RIGHT_EYE_OPEN = 0.5;
    private static final double SMILING = 0.15;

    /**
     * Method for detecting faces in a bitmap.
     *
     * @param context The application context.
     * @param picture The picture in which to detect the faces.
     */
    static void detectFaces(Context context, Bitmap picture) {

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

        // If there are no faces detected, show a Toast message
        if(faces.size() == 0)
        {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Log classifications for each face
            for (int i = 0; i < faces.size(); i++)
            {
                Face face = faces.valueAt(i);

                // Log classifications for face
                whichEmoji(face);
            }
        }

        // Release the detector
        detector.release();
    }

    static public void whichEmoji(Face face)
    {
        Log.d(LOG_TAG, "CLASSIFICATIONS: isLeftEyeOpenProb" + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "CLASSIFICATIONS: isRightRyeOpenProb" + face.getIsRightEyeOpenProbability());
        Log.d(LOG_TAG, "CLASSIFICATIONS: isSmilingProb" + face.getIsSmilingProbability());

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

        Emoji emoji;
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
    }
}