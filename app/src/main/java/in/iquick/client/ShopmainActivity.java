package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;

import java.util.ArrayList;
import java.util.List;

public class ShopmainActivity extends AppCompatActivity {

    ImageCarousel carousel;
    List<CarouselItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopmain);

        carousel = findViewById(R.id.carouselSM);

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }


}