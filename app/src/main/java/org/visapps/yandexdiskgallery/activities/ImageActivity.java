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

    // ViewPager, наследуемый от стандартного ViewPager с обработкой ошибки при зуммировании (см. https://github.com/chrisbanes/PhotoView)
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
            // Если активити было пересоздано, получаем сохраненную позицию для View Pager
            currposition = savedInstanceState.getInt("currposition");
        } else {
            // Если первый запуск активити, получаем из bundle позицию картинки, которую нужно показать
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currposition = extras.getInt("position");
            }
        }
        // Инициализируем View Pager
        viewPager = findViewById(R.id.viewPager);
        // Добавляем обработчик смены страницы
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // При выборе новой страницы обновляем текст в тулбаре
                updateToolbar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // Инициализиурем View Model
        initViewModel();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // При уничтожении активити сохраняем текущую позицию View Pager
        super.onSaveInstanceState(outState);
        outState.putInt("currposition", viewPager.getCurrentItem());
    }

    private void initViewModel(){
        // Инициализиурем View Model
        viewModel = new ImageActivityViewModel();
        // Подписываемся на получение данных из БД
        viewModel.getItemsObservable().observe(this, items -> {
            if(items !=null){
                // Помещаем данные в адаптер
                adapter = new ImageSliderAdapter(this, items);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(currposition);
                // Помещаем статус-текст в тулбар
                updateToolbar();
            }
        });
    }

    private void updateToolbar(){
        // Обновляем текст в тулбаре
        getSupportActionBar().setTitle(String.valueOf(viewPager.getCurrentItem()+1) + " " + getString(R.string.of) + " " + String.valueOf(adapter.getCount()));
    }

}
