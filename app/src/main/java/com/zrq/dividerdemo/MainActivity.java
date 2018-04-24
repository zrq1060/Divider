package com.zrq.dividerdemo;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zrq.divider.Divider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText inputNumEditText;
    private RecyclerView recyclerView;
    private TestAdapter adapter;
    private RecyclerView.LayoutManager linearLayoutManager;
    private List<View> headerViews = new ArrayList<>();
    private List<View> footerViews = new ArrayList<>();
    private Divider divider;
    private int num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputNumEditText = findViewById(R.id.et_input_num);
        recyclerView = findViewById(R.id.recyclerView);
        // 初始化监听
        initListener();
        // 设置adapter
        adapter = new TestAdapter(R.layout.item_test, null);
        recyclerView.setAdapter(adapter);
        divider = Divider.builder().color(Color.BLUE).width(10).height(20).build();
        recyclerView.addItemDecoration(divider);
        // 设置manager
        switchLayoutManager(1);
        // 设置数据
        replaceData(10);
    }

    private void initListener() {
        // 文字改变监听
        inputNumEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String trim = inputNumEditText.getText().toString().trim();
                if (TextUtils.isEmpty(trim))
                    return;
                num = Integer.parseInt(trim);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 设置内容数量
        findViewById(R.id.btn_set_num).setOnClickListener(v -> replaceData(num));
        // 增加内容数量
        findViewById(R.id.btn_add_num).setOnClickListener(v -> addData(num));
        // 删除内容数量
        findViewById(R.id.btn_remove_num).setOnClickListener(v -> removeData(num));
        // 增加头
        findViewById(R.id.btn_header_add).setOnClickListener(v -> {
            View headerView = getHeaderViewOrFooterView();
            adapter.addHeaderView(headerView);
            headerViews.add(headerView);
            divider.setHeaderCount(adapter.getHeaderLayoutCount());
            redrawDivider();// 增删得重新的绘制线
        });
        // 移除头
        findViewById(R.id.btn_header_remove).setOnClickListener(v -> {
            if (headerViews.size() > 0) {
                adapter.removeHeaderView(headerViews.get(0));
                headerViews.remove(0);
                divider.setHeaderCount(adapter.getHeaderLayoutCount());
                redrawDivider();// 增删得重新的绘制线
            }
        });
        // 增加尾
        findViewById(R.id.btn_footer_add).setOnClickListener(v -> {
            View headerView = getHeaderViewOrFooterView();
            adapter.addFooterView(headerView);
            footerViews.add(headerView);
            divider.setFooterCount(adapter.getFooterLayoutCount());
            redrawDivider();// 增删得重新的绘制线
        });
        // 移除尾
        findViewById(R.id.btn_footer_remove).setOnClickListener(v -> {
            if (footerViews.size() > 0) {
                adapter.removeFooterView(footerViews.get(0));
                footerViews.remove(0);
                divider.setFooterCount(adapter.getFooterLayoutCount());
                redrawDivider();// 增删得重新的绘制线
            }
        });
        // 切换LayoutManager
        findViewById(R.id.btn_linear_v).setOnClickListener(v -> switchLayoutManager(1));
        findViewById(R.id.btn_linear_h).setOnClickListener(v -> switchLayoutManager(2));
        findViewById(R.id.btn_grid_v).setOnClickListener(v -> switchLayoutManager(3));
        findViewById(R.id.btn_grid_h).setOnClickListener(v -> switchLayoutManager(4));
        findViewById(R.id.btn_staggered_v).setOnClickListener(v -> switchLayoutManager(5));
        findViewById(R.id.btn_staggered_h).setOnClickListener(v -> switchLayoutManager(6));
    }

    /**
     * 切换LayoutManager
     */
    private void switchLayoutManager(int layoutManagerState) {
        switch (layoutManagerState) {
            case 1:
                // LinearLayoutManager VERTICAL
                linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                break;
            case 2:
                // LinearLayoutManager HORIZONTAL
                linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                break;
            case 3:
                // GridLayoutManager VERTICAL
                GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 4, LinearLayoutManager.VERTICAL, false);
                this.linearLayoutManager = gridLayoutManager;
                setSpanSizeLookup(gridLayoutManager);// 设置头、尾，占用3列
                break;
            case 4:
                // GridLayoutManager HORIZONTAL
                GridLayoutManager gridLayoutManagerHorizontal = new GridLayoutManager(getApplicationContext(), 4, LinearLayoutManager.HORIZONTAL, false);
                this.linearLayoutManager = gridLayoutManagerHorizontal;
                setSpanSizeLookup(gridLayoutManagerHorizontal);// 设置头、尾，占用3列
                break;
            case 5:
                // StaggeredGridLayoutManager VERTICAL
                this.linearLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
                break;
            case 6:
                // StaggeredGridLayoutManager HORIZONTAL
                this.linearLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.HORIZONTAL);
                break;
        }
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setLayoutManagerState(layoutManagerState);
        adapter.notifyDataSetChanged();
    }

    /**
     * GridLayoutManager 设置头、尾，占用3列
     */
    private void setSpanSizeLookup(GridLayoutManager gridLayoutManager) {
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position < adapter.getHeaderLayoutCount() || position >= adapter.getItemCount() - adapter.getFooterLayoutCount())
                    // 头或者尾部，占用3列
                    return gridLayoutManager.getSpanCount();
                else
                    return 1;
            }
        });
    }

    /**
     * 获取增加的头、尾
     */
    private View getHeaderViewOrFooterView() {
        View headerView = View.inflate(getApplicationContext(), R.layout.item_header_footer, null);
        headerView.setBackgroundColor(Color.GREEN);
        headerView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return headerView;
    }

    /**
     * 置换数据
     */
    private void replaceData(int itemCount) {
        ArrayList<String> items = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            items.add("原始数据" + i);
        }
        adapter.replaceData(items);
    }

    /**
     * 增数据
     */
    private void addData(int position) {
        if (position <= adapter.getData().size()) {
            adapter.addData(position, "新增数据");
            redrawDivider();// 增删得重新的绘制线
        }
    }

    /**
     * 删数据
     */
    private void removeData(int position) {
        if (position < adapter.getData().size()) {
            adapter.remove(position);
            redrawDivider();// 增删得重新的绘制线
        }
    }

    /**
     * 重新绘制线
     */
    private void redrawDivider() {
        // 如果增删调用的是  adapter.notifyDataSetChanged() 则不用调用下面方法
        recyclerView.invalidateItemDecorations();
    }
}
