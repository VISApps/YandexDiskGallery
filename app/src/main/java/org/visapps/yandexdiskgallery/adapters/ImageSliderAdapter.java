package org.visapps.yandexdiskgallery.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.chrisbanes.photoview.PhotoView;

import org.visapps.yandexdiskgallery.R;
import org.visapps.yandexdiskgallery.YandexDiskGallery;
import org.visapps.yandexdiskgallery.models.DiskItem;
import org.visapps.yandexdiskgallery.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class ImageSliderAdapter extends PagerAdapter {

    private List<DiskItem> items;
    private Context context;

    public ImageSliderAdapter(Context context,List<DiskItem> items){
        this.context = context;
        this.items = new ArrayList<>();
        this.items.addAll(items);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        DiskItem item = items.get(position);
        PhotoView photoView = new PhotoView(view.getContext());
        String token = YandexDiskGallery.getInstance().getSharedPrefsService().getToken();
        GlideUrl glideUrl = new GlideUrl(item.getPreview(), new LazyHeaders.Builder()
                .addHeader("Authorization", "OAuth " + token)
                .build());
        GlideApp.with(context).load(glideUrl).transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_broken_image_gray_24dp)
                .into(photoView);
        view.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
