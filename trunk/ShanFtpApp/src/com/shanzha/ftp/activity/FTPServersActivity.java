package com.shanzha.ftp.activity;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FTPServersActivity extends ListActivity {
	private static final int TRY_TO_CONNECT = 0;
	private ArrayList<String> serverIp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serverIp = new ArrayList<String>();
		serverIp.add("手动输入");
		Intent intent = getIntent();
		serverIp.addAll(intent.getExtras().getStringArrayList("serverip"));
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, serverIp);
		setListAdapter(adapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent intent = new Intent(this, ConnectActivity.class);
		String item = serverIp.get(position);
		if (!"手动输入".equals(item)) {
			intent.putExtra("ip", item);
		}
		startActivityForResult(intent, TRY_TO_CONNECT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TRY_TO_CONNECT) {
			if (resultCode == RESULT_OK) {
				// OK
				setResult(RESULT_OK);
				this.finish();
				// startActivity(new Intent(this, IndexActivity.class));
			} else {
				// FALID
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
