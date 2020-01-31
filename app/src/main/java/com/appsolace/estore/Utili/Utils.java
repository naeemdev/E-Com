package com.appsolace.estore.Utili;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;


public class Utils {

    public static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    public static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    public static final OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4);

    public static Bitmap getCircularBitmap(Bitmap bitmap){

        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;

    }

    public static boolean containsWhitespace(String str) {
        if (!hasLength(str)) {
            return false;
        }
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLength(String str) {
        return (str != null && str.length() > 0);
    }

    public static String Getcolorname(String code) {

        String name = "";
        if (code.equals("#000000")) {
            name = "Black";
        } else if (code.equals("#FF00FF")) {
            name = "Multicolor";
        } else if (code.equals("#FF0000")) {
            name = "Red";
        } else if (code.equals("#FFFFFF")) {
            name = "White";
        } else if (code.equals("#A52A2A")) {
            name = "Brown";
        } else if (code.equals("#808080")) {
            name = "Gray";
        } else if (code.equals("#FFC0CB")) {
            name = "Pink";
        } else if (code.equals("#0000FF")) {
            name = "Blue";
        } else if (code.equals("#4B0082")) {
            name = "Indigo";
        } else if (code.equals("#800000")) {
            name = "Maroon";
        } else if (code.equals("#EE82EE")) {
            name = "Violet";
        } else if (code.equals("#FFFF00")) {
            name = "Yellow";
        } else if (code.equals("#000080")) {
            name = "Navy";
        } else if (code.equals("#008000")) {
            name = "Green";
        } else if (code.equals("#90EE90")) {
            name = "Light Green";
        } else if (code.equals("#FFA500")) {
            name = "Orange";
        } else if (code.equals("#F5F5DC")) {
            name = "Beige";
        }
        else
        {
            name = "Black";
        }

        return name;
    }


    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPass(String s) {
        String n = ".*[0-9].*";
        String a = ".*[a-zA-Z].*";
        return s.matches(n) && s.matches(a);
    }
}
