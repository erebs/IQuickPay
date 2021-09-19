package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

    }

    public void Logout(View view)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("id", "");
        editor.putString("name", "");
        editor.putString("phone","");
        editor.putString("shopname", "");
        editor.putString("comm_plan", "");
        editor.apply();
        Intent intents = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

}