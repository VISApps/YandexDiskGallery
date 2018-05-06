package org.visapps.yandexdiskgallery.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.visapps.yandexdiskgallery.R;
import org.visapps.yandexdiskgallery.YandexDiskGallery;
import org.visapps.yandexdiskgallery.models.DiskItem;
import org.visapps.yandexdiskgallery.utils.GlideApp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;

public class DiskItemsAdapter extends RecyclerView.Adapter<DiskItemsAdapter.ViewHolder>{

    public interface  ItemsCallback{
        void onClick(int position);
    }

    private List<DiskItem> items;
    private Context context;
    private ItemsCallback callback;


    public void setCallback(ItemsCallback callback) {
        this.callback = callback;
    }

    public DiskItemsAdapter(Context context){
        this.context = context;
        items = new ArrayList<>();
    }

    public void setItems(List<DiskItem> items){
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }


    public void addItems(List<DiskItem> items){
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }


    @Override
    public DiskItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DiskItemsAdapter.ViewHolder holder, int position) {
        DiskItem item = items.get(position);
        String token = YandexDiskGallery.getInstance().getSharedPrefsService().getToken();
        GlideUrl glideUrl = new GlideUrl(item.getPreview(), new LazyHeaders.Builder()
                .addHeader("Authorization", "OAuth " + token)
                .build());
        GlideApp.with(context).load(glideUrl).transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_broken_image_gray_24dp)
                .centerCrop()
                .into(holder.preview);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView preview;
        View diskitem;


        public ViewHolder(View itemView) {
            super(itemView);
            preview = itemView.findViewById(R.id.preview);
            diskitem = itemView.findViewById(R.id.diskitem);
            diskitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onClick(getAdapterPosition());
                }
            });
        }



    }

}
