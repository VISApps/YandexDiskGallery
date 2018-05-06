package org.visapps.yandexdiskgallery.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.visapps.yandexdiskgallery.R;
import org.visapps.yandexdiskgallery.adapters.DiskItemsAdapter;
import org.visapps.yandexdiskgallery.utils.GlideApp;
import org.visapps.yandexdiskgallery.utils.UIHelper;
import org.visapps.yandexdiskgallery.viewmodels.MainActivityViewModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Код для создания интента активити авторизации
    public static final int REQUEST_AUTH = 1001;

    // View Model
    private MainActivityViewModel viewModel;
    private DiskItemsAdapter adapter;

    private ConstraintLayout rootlayout;
    private RecyclerView itemslist;
    private SwipeRefreshLayout refresher;
    private ProgressBar loadingprogress;
    private TextView name, email;
    private ImageView avatar;

    // Флаг, означающий что в данный момент подгружается новая страница данных
    private boolean isloading=false;
    // Состояние RecyclerView для записи и чтеняи из Bundle
    private Parcelable itemsliststate = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        rootlayout = findViewById(R.id.rootlayout);
        name = navigationView.getHeaderView(0).findViewById(R.id.name);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);
        avatar = navigationView.getHeaderView(0).findViewById(R.id.avatar);
        loadingprogress = findViewById(R.id.loadingprogress);
        refresher = findViewById(R.id.refresher);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewModel.refreshItems();
            }
        });
        itemslist = findViewById(R.id.itemslist);
        adapter = new DiskItemsAdapter(this);
        //Вызываем статический метод calculateNoOfColumns для расчета количества колонок в GridLayoutManager для данного разрешения экрана
        GridLayoutManager layoutManager = new GridLayoutManager(this, UIHelper.calculateNoOfColumns(getApplicationContext()));
        itemslist.setLayoutManager(layoutManager);
        itemslist.setAdapter(adapter);
        // Добавляем обработчик подгрузки данных
        itemslist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Если RecyclerView был пролистан до конца и данные еще не подгружаются, обращаемся к VIew Model за новой страницей
                int totalItemCount = adapter.getItemCount()-1;
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                if (!isloading && totalItemCount <= lastVisibleItem && adapter.getItemCount()>0) {
                    viewModel.loadmoreItems();
                }
            }
        });
        // Добавляем callback для обработки нажатия на элемент RecyclerView
        adapter.setCallback(new DiskItemsAdapter.ItemsCallback() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, ImageActivity.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        // Получаем View Model для Main Activity
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        if (savedInstanceState != null) {
            // Предоставляем View Model данные их сохраненного состояния
            boolean dataloaded = savedInstanceState.getBoolean("dataloaded");
            boolean havemore = savedInstanceState.getBoolean("havemore");
            viewModel.setDataloaded(dataloaded);
            viewModel.setHavemore(havemore);
            // Получаем сохраненного состояние RecyclerView
            itemsliststate = savedInstanceState.getParcelable("itemsliststate");
        }
        // Инициализируем подписки на данные
        initObservers();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // При уничтожении активити сохраняем состояние View Model и RecyclerView
        super.onSaveInstanceState(outState);
        outState.putBoolean("dataloaded", viewModel.isDataloaded());
        outState.putBoolean("havemore", viewModel.isHavemore());
        itemsliststate = itemslist.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("itemsliststate",itemsliststate);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_about) {
            // Запускаем активити с информацией о приложении
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_switchacc) {
            // Запрашиваем у View Model смену аккаунта
            viewModel.logout();
        } else if (id == R.id.nav_exit) {
            // Полностью завершаем приложение
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==REQUEST_AUTH){
            if(resultCode==RESULT_OK){
                // Если из активити авторизации пришел результат об успешной атворизации, обращаемся к View Model для инициализации загрузки данных для нового аккаунта
                viewModel.init();
            }
            else{
                // Если активити авторизации было завершено без результата, закрываем приложение, так как авторизация обязательна
                finish();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initObservers(){
        // Подписываемся на запуск активити авторизации в случае выхода из аккаунта
        viewModel.getAuthObservable().observe(this, auth -> {
            if(auth != null){
                if(!auth){
                    Intent intent = new Intent(this, AuthActivity.class);
                    startActivityForResult(intent,REQUEST_AUTH);
                }
            }
        });
        // Подписываемся на отображение информации об ошибках
        viewModel.getErrorObservable().observe(this, error -> {
            if(error != null){
                String message = "";
                switch (error){
                    case ServerError:
                        message = getString(R.string.servererror);
                        break;
                    case ServiceUnavailable:
                        message = getString(R.string.serviceunavailable);
                        break;
                    case NetworkError:
                        message = getString(R.string.networkerror);
                        break;
                    case UnknownError:
                        message = getString(R.string.unknownerror);
                        break;
                }
                Snackbar.make(rootlayout, getString(R.string.unabletoload) + ": " + message, Snackbar.LENGTH_SHORT).show();
            }
        });
        // Подписываемся на обновление состояния SwipeRefreshLayout
        viewModel.getRefresherObservable().observe(this, refreshing -> {
            if(refreshing !=null){
                refresher.setRefreshing(refreshing);
                // Если данные обновляются, также скрываем RecyclerView
                if(refreshing){
                    itemslist.setVisibility(View.GONE);
                }
                else{
                    itemslist.setVisibility(View.VISIBLE);
                }
            }
        });
        // Подписываемся на обновление данных аккаунта
        viewModel.getPassportObserbable().observe(this,response -> {
            if(response != null){
                name.setText(response.getRealName());
                email.setText(response.getDefaultEmail());
                GlideApp.with(this).load("https://avatars.yandex.net/get-yapic/" + response.getDefaultAvatarId() + "/islands-200")
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE).into(avatar);
            }
        });
        // Подписываемся на обновления состояния индикатора подгрузки изображений
        viewModel.getLoadMoreObservable().observe(this,loading ->{
            if(loading != null){
                isloading = loading;
                if(loading){
                    loadingprogress.setVisibility(View.VISIBLE);
                }
                else{
                    loadingprogress.setVisibility(View.GONE);
                }
            }
        });
        // Подписваемся на обновления изображений из БД
        viewModel.getItemsObservable().observe(this, items -> {
            if(items != null){
                adapter.setItems(items);
                if(itemsliststate != null){
                    itemslist.getLayoutManager().onRestoreInstanceState(itemsliststate);
                    itemsliststate = null;
                }
            }
        });
    }
}
