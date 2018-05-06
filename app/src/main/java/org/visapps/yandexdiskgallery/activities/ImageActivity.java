package org.visapps.yandexdiskgallery.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.visapps.yandexdiskgallery.R;
import org.visapps.yandexdiskgallery.adapters.ImageSliderAdapter;
import org.visapps.yandexdiskgallery.utils.GalleryViewPager;
import org.visapps.yandexdiskgallery.viewmodels.ImageActivityViewModel;

public class ImageActivity extends AppCompatActivity {

    private GalleryViewPager viewPager;
    private int currposition=0;

    private ImageActivityViewModel viewModel;
    private ImageSliderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (savedInstanceState != null) {
            currposition = savedInstanceState.getInt("currposition");
        } else {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currposition = extras.getInt("position");
            }
        }
        viewPager = findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateToolbar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        initViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currposition", viewPager.getCurrentItem());
    }

    private void initViewModel(){
        viewModel = new ImageActivityViewModel();
        viewModel.getItemsObservable().observe(this, items -> {
            if(items !=null){
                adapter = new ImageSliderAdapter(this, items);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(currposition);
                updateToolbar();
            }
        });
    }

    private void updateToolbar(){
        getSupportActionBar().setTitle(String.valueOf(viewPager.getCurrentItem()+1) + " " + getString(R.string.of) + " " + String.valueOf(adapter.getCount()));
    }

}
