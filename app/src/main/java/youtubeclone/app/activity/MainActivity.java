package youtubeclone.app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import youtubeclone.app.R;
import youtubeclone.app.adapter.AdapterVideo;
import youtubeclone.app.api.YoutubeService;
import youtubeclone.app.helper.RetrofitConfig;
import youtubeclone.app.helper.YoutubeConfig;
import youtubeclone.app.listener.RecyclerItemClickListener;
import youtubeclone.app.model.Item;
import youtubeclone.app.model.Resultado;
import youtubeclone.app.model.Video;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerViewVideos;
    private MaterialSearchView searchView;

    private List<Item> listVideos = new ArrayList<>();
    private Resultado resultado;
    private AdapterVideo adapterVideo;

    private Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewVideos = findViewById(R.id.recyclerViewVideos);
        searchView = findViewById(R.id.searchView);

        retrofit = RetrofitConfig.getRetrofit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Youtube Clone");
        setSupportActionBar(toolbar);

        recuperarVideos("");


        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                recuperarVideos("");
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                recuperarVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void recuperarVideos(String pesquisa){

        String q = pesquisa.replaceAll(" ", "+");

        YoutubeService youtubeService = retrofit.create(YoutubeService.class);

        youtubeService.recuperarVideos("snippet", "date", "25",
                YoutubeConfig.YOUTUBE_API_KEY, YoutubeConfig.CANAL_ID, q).enqueue(new Callback<Resultado>() {
            @Override
            public void onResponse(Call<Resultado> call, Response<Resultado> response) {
                if (response.isSuccessful()){
                    resultado = response.body();
                    listVideos = resultado.items;

                    configurarRecicleView();
                }
            }

            @Override
            public void onFailure(Call<Resultado> call, Throwable t) {

            }
        });
    }

    public void configurarRecicleView(){
        adapterVideo = new AdapterVideo(listVideos, this);
        recyclerViewVideos.setHasFixedSize(true);
        recyclerViewVideos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVideos.setAdapter(adapterVideo);

        recyclerViewVideos.addOnItemTouchListener(new RecyclerItemClickListener(this,
                recyclerViewVideos, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Item video = listVideos.get(position);
                String idVideo = video.id.videoId;

                Intent intent = new Intent(MainActivity.this, PlayActivity.class);
                intent.putExtra("idVideo", idVideo);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(menuItem);

        return true;
    }
}