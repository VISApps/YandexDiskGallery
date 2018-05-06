package org.visapps.yandexdiskgallery.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GalleryViewPager extends ViewPager{

    // Класс, наследуемый от стандартного View Pager

    public GalleryViewPager(Context context){
        super(context);
    }

    public GalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Обрабатываем ошибку, возникающую при сильном зуммировании внутри View Pager (см. https://github.com/chrisbanes/PhotoView)
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
