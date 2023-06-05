package test.exoplayertest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import test.exoplayertest.R;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import test.exoplayertest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CHOOSE_VIDEO = 564;
    private SimpleExoPlayer player;
    private ActivityMainBinding binding;
    private PlayerView playerView;
    private TextureView textureView;
    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        player = new SimpleExoPlayer.Builder(this).build();

        playerView = findViewById(R.id.player_view);
        playerView.setPlayer(player);

        textureView = new TextureView(this);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                player.setVideoSurface(new Surface(surfaceTexture));
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                Size size = getSizeForDesiredSize(textureView.getWidth(), textureView.getHeight(), 500);
               binding.imageView.setImageBitmap(textureView.getBitmap(size.getWidth(), size.getHeight()));
                Log.e(TAG, "onSurfaceTextureUpdated: surface updated" );
            }
        });

        FrameLayout contentFrame = playerView.findViewById(com.google.android.exoplayer2.R.id.exo_content_frame);
        View videoFrameView = textureView;
        if(videoFrameView != null) contentFrame.addView(videoFrameView);


        binding.btnSelectVideo.setOnClickListener( v-> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_CHOOSE_VIDEO);

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHOOSE_VIDEO && resultCode == RESULT_OK) {
            MediaItem mediaItem = MediaItem.fromUri(data.getData());
            player.stop();
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }
    }

    protected Size getSizeForDesiredSize(int width, int height, int desiredSize){
        int w, h;
        if(width > height){
            w = desiredSize;
            h = Math.round((height/(float)width) * w);
        }else{
            h = desiredSize;
            w = Math.round((width/(float)height) * h);
        }
        return new Size(w, h);
    }
}