package org.visapps.yandexdiskgallery.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class UIHelper {

    // Метод для расчета количества колонок для конретного разрешения экрана
    // https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns/38472370
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        return noOfColumns;
    }
}
