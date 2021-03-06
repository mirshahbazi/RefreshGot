package mrfu.swiperefreshboth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import mrfu.refreshgot.GotListView;
import mrfu.refreshgot.GotRefresh;
import mrfu.refreshgot.utils.PullRefreshListener;

/**
 * Created by MrFu on 16/3/21.
 */
public class LxListViewActivity extends AppCompatActivity implements PullRefreshListener {

    private ArrayList<String> list = new ArrayList<>();

    @Bind(R.id.lx_refresh)
    GotRefresh mGotRefresh;
    @Bind(R.id.lx_listview)
    GotListView mGotListView;
    private ArrayAdapter arrayAdapter;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lxlistview);
        ButterKnife.bind(this);

        initTitleBar();

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);//getData()
        mGotRefresh.setOnPullRefreshListener(this);
        mGotListView.setAdapter(mGotRefresh, arrayAdapter);

        firstLoadTestData();
    }


    private void initTitleBar() {
        toolbar.setTitle("LxListView");
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        toolbar.setLogo(R.mipmap.logo);
        toolbar.inflateMenu(R.menu.menu_list);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                                               @Override
                                               public boolean onMenuItemClick(MenuItem item) {
                                                   Uri uri = Uri.parse("https://github.com/MrFuFuFu/SwipeRefreshBoth");
                                                   Intent i = new Intent(Intent.ACTION_VIEW, uri);
                                                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                   startActivity(i);
                                                   return false;
                                               }
                                           }
        );
    }

    boolean isPullDown = false;

    @Override
    public void onPullDownRefresh() {
        isPullDown = true;
        index = 3;
        loadTestData();
    }

    @Override
    public void onPullUpRefresh() {
        isPullDown = false;
        if (index > 0){
            loadTestData();
            index --;
        }else {
            Toast.makeText(this, "No more data", Toast.LENGTH_SHORT).show();
            mGotRefresh.refreshReset();
        }
    }

    int index = 3;


    private void firstLoadTestData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //load finish do it
                list.addAll(getData());
                arrayAdapter.notifyDataSetChanged();
            }
        }, 500);
    }

    private void loadTestData(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //load finish do it
                if (isPullDown){
                    arrayAdapter.clear();
                    arrayAdapter.addAll(getData());
                }else {
                    arrayAdapter.addAll(getData());
                }
                arrayAdapter.notifyDataSetChanged();
                mGotRefresh.refreshReset();
            }
        }, 500);
    }

    private ArrayList<String> getData() {
        ArrayList<String> list = new ArrayList<>();
        list.add(count++ + "  Yuan Fu");
        list.add(count++ + "  Yuan Fu");
        list.add(count++ + "  http://mrfu.me/");
        list.add(count++ + "  GitHub: MrFuFuFu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  Yuan Fu");
//        list.add(count++ + "  http://mrfufufu.github.io/");
//        list.add(count++ + "  GitHub: MrFuFuFu");
        return list;
    }
}
