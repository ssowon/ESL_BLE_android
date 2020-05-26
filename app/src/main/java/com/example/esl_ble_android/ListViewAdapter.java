package com.example.esl_ble_android;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>() ;

    public ListViewAdapter(ArrayList<ListViewItem> data) {
        this.listViewItemList = data;
    }

    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView itemImageView = convertView.findViewById(R.id.image_item);
        TextView nameTextView = convertView.findViewById(R.id.tv_name) ;
        TextView priceTextView = convertView.findViewById(R.id.price) ;
        TextView posTextView = convertView.findViewById(R.id.pos) ;
        TextView numberTextView = convertView.findViewById(R.id.number) ;
        TextView saleTextView = convertView.findViewById(R.id.sale) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = (ListViewItem) getItem(position);

        // 아이템 내 각 위젯에 데이터 반영
        nameTextView.setText(listViewItem.getName());
        priceTextView.setText(listViewItem.getPrice());
        posTextView.setText(listViewItem.getPos());
        numberTextView.setText(listViewItem.getNumber());
        saleTextView.setText(listViewItem.getSale());

        Picasso.get()
                .load("http://ec2-13-124-77-109.ap-northeast-2.compute.amazonaws.com" + "/image/" + listViewItem.getNumber() + ".png")
                .into(itemImageView);

        return convertView;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String name, String price, String pos, String number, String sale) {
        ListViewItem item = new ListViewItem(name,price,pos,number,sale);

        item.setName(name);
        item.setPrice(price);
        item.setPos(pos);
        item.setNumber(number);
        item.setSale(sale);

        listViewItemList.add(item);
    }
}