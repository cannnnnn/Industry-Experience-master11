package com.iteration1.savingwildlife.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class UIUtils {
    private static Toast toast;
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    // This method is to adjust the size of imageview
    // @Return: the layout params of the viewgroup
    public static ViewGroup.LayoutParams adjustImageSize(Drawable r, View v) {
        // First get width and height of this drawable
        int width = r.getIntrinsicWidth();
        int height = r.getIntrinsicHeight();
        // Calculate the scale ratio of this drawable
        double ratio = (double) width / height;

        // Get layout params of this imageview
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        // Calculate the actual width in device first, and the height accordingly
        layoutParams.width = (int) Math.floor(Resources.getSystem().getDisplayMetrics().widthPixels * 0.95);
        layoutParams.height = (int) Math.floor(Resources.getSystem().getDisplayMetrics().widthPixels / ratio + 0.5);
        // Set the according height/weight to this imageview

        return layoutParams;
    }


    public static void showCenterToast(Context context,
                                       String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        // Convert Map to List of Map
        List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sort list with Collections.sort(), provide a custom Comparator
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


}
