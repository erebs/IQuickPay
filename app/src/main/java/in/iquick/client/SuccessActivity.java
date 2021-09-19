package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Type;

public class SuccessActivity extends AppCompatActivity {

    ImageView TypeImage,SuccessImage;
    TextView RechargeStatus,SuccessYText;
    String Type,Number,Amount,Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        RechargeStatus = findViewById(R.id.RechargeStatus);
        TypeImage = findViewById(R.id.TypeImage);
        SuccessYText = findViewById(R.id.SuccessYText);
        SuccessImage = findViewById(R.id.SuccessImage);

        try {

            Intent intent = getIntent();
            Type=intent.getStringExtra("Type");
            Number=intent.getStringExtra("Number");
            Amount=intent.getStringExtra("Amount");
            Status=intent.getStringExtra("Status");

            RechargeStatus.setText(Type+(Type.equalsIgnoreCase("Postpaid")?" Bill payment":" Recharge"));
            SuccessYText.setText("Your "+Type+(Type.equalsIgnoreCase("Postpaid")?" Bill payment":" Recharge")
                    +" of Rs."+Amount+" towards the number "+Number+" is Processing...");

            if(Type.equalsIgnoreCase("Prepaid"))
            {
                TypeImage.setImageResource(R.drawable.il_prepaid);
            }
            else if(Type.equalsIgnoreCase("Postpaid"))
            {
                TypeImage.setImageResource(R.drawable.il_postpaid);
            }
            else
            {
                TypeImage.setImageResource(R.drawable.il_dth);
            }

            if(Status.equalsIgnoreCase("Success"))
            {
                SuccessImage.setBackground(getDrawable(R.drawable.success));
                SuccessImage.setImageResource(R.drawable.outline_check_24);
            }
            else if(Status.equalsIgnoreCase("Failed"))
            {
                SuccessImage.setBackground(getDrawable(R.drawable.danger));
                SuccessImage.setImageResource(R.drawable.outline_do_not_disturb_24);
            }
            else if(Status.equalsIgnoreCase("Pending"))
            {
                SuccessImage.setBackground(getDrawable(R.drawable.pending));
                SuccessImage.setImageResource(R.drawable.outline_thumb_up_off_alt_24);
            }
            else
            {
                SuccessImage.setBackground(getDrawable(R.drawable.pending));
                SuccessImage.setImageResource(R.drawable.outline_schedule_24);
            }

        }catch (Exception e)
        {
        }

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void Exit(View view)
    {
        finish();
    }

}