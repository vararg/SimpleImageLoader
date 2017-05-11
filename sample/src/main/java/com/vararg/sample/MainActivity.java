package com.vararg.sample;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vararg.imageloader.ImageLoader;
import com.vararg.sample.widgets.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    private ItemsAdapter itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initRefreshLayout();
        initList();
        fillList();
    }

    private void initRefreshLayout() {
        //clear image loader caches and reset list data
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ImageLoader.with(MainActivity.this)
                        .clearCache(MainActivity.this);
                fillList();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void initList() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        itemsAdapter = new ItemsAdapter();
        recyclerView.setAdapter(itemsAdapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelOffset(R.dimen.item_list_space)));

    }

    private void fillList() {
        itemsAdapter.setItems(generateItems());
    }

    private List<ItemViewModel> generateItems() {
        List<ItemViewModel> items = new ArrayList<>();

/*        for (int i = 0; i < 24; i++) {
            items.add(new ItemViewModel("sasd"));
        }*/

        // 18 items with different sizes but similar ratio

        //500 x 743
        items.add(new ItemViewModel("http://www.stmphotography.ca/uploads/2/6/0/6/26065498/_7731176_orig.jpg"));
        //1348 × 2000
        items.add(new ItemViewModel("https://s-media-cache-ak0.pinimg.com/originals/dc/85/13/dc8513317defb020d4c351984796d67a.jpg"));
        //600 × 890
        items.add(new ItemViewModel("https://www.smashingmagazine.com/images/movie-posters/8.jpg"));
        //414 × 612
        items.add(new ItemViewModel("http://media.gettyimages.com/photos/poster-for-the-movie-the-terminator-1984-picture-id154950042?s=612x612"));
        //600 × 848
        items.add(new ItemViewModel("https://s-media-cache-ak0.pinimg.com/736x/9d/a9/39/9da9399617804fd5a4a8bd1c755762d3.jpg"));
        //2025 × 3000
        items.add(new ItemViewModel("http://assets.cdn.moviepilot.de/files/ce9fa2011edf07178a5f7e697dc39898eec4d0e9122918536cce4c2ead88/synchronicity-poster.jpg"));
        //1280 × 1896
        items.add(new ItemViewModel("http://24.media.tumblr.com/tumblr_m8m73gC7U61r9626to1_1280.jpg"));
        //520 × 771
        items.add(new ItemViewModel("http://img.moviepostershop.com/the-magnificent-seven-movie-poster-2016-1020776374.jpg"));
        //872 × 1280
        items.add(new ItemViewModel("https://s-media-cache-ak0.pinimg.com/originals/27/67/56/276756c78e41f19d7e2c95bba12a8e2d.jpg"));
        //620 × 918
        items.add(new ItemViewModel("https://s3.amazonaws.com/seat42faws1/wp-content/uploads/2013/11/12120926/NOAH-Movie-Poster.jpg"));
        //500 × 741
        items.add(new ItemViewModel("http://filmonic.com/wp-content/uploads/2010/11/paul-movie-poster.jpg"));
        //723 × 1104
        items.add(new ItemViewModel("http://pre11.deviantart.net/0773/th/pre/f/2016/026/c/f/deadpool_x_green_lantern_movie_poster_by_m7781-d9petjy.jpg"));
        //580 × 900
        items.add(new ItemViewModel("http://img.moviepostershop.com/the-faculty-movie-poster-1998-1020191998.jpg"));
        //1383 × 2048
        items.add(new ItemViewModel("https://amyrosecrumptonmpp.files.wordpress.com/2014/11/official-the-hunger-games-movie-poster-the-hunger-games-movie-23911630-1383-2048.jpg"));
        //1000 × 1481
        items.add(new ItemViewModel("http://cdn.collider.com/wp-content/uploads/hugo-movie-poster-02.jpg"));
        //736 × 1073
        items.add(new ItemViewModel("https://s-media-cache-ak0.pinimg.com/736x/63/35/b3/6335b33481b913f437b4e395cf71f9b6.jpg"));
        //1564 × 2348
        items.add(new ItemViewModel("http://netdna.webdesignerdepot.com/uploads/2012/12/newhope.jpg"));
        //640 × 905
        items.add(new ItemViewModel("https://s-media-cache-ak0.pinimg.com/736x/fd/5e/66/fd5e662dce1a3a8cd192a5952fa64f02.jpg"));

        return items;
    }
}
