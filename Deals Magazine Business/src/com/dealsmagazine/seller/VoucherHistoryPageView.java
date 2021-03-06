/*
 * 
 * � 2011 WhereYouShop.com  All Rights Reserved | http://www.whereyoushop.com 
 * Emergency 24    | The WhereYouShop Team  
 *
 */

package com.dealsmagazine.seller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dealsmagazine.util.ExternalStorageUtils;
import com.dealsmagazine.util.HtmlUtils;
import com.dealsmagazine.util.VoucherHistoryPageUtils;
import com.dealsmagazine.util.VoucherInfoUtils;
import com.dealsmagazine.seller.R;

public class VoucherHistoryPageView extends Activity {

	public final static String KEY_VOUCHER_HISTORY_PAGE_CODE = "VOUCHER_HISTORY_PAGE_CODE";

	static private Context appContext;

	private AlertDialog.Builder ad_delete_confirm;
	private Button btn_vhp_delete;
	private Button btn_vhp_back;
	private TextView tv_voucher_code;
	private TextView tv_buyer_name;
	private TextView tv_voucher_redeem_date;
	private TextView tv_title;
	private TextView tv_price;
	private TextView tv_value;
	private TextView tv_purchased_date;
	private TextView tv_fine_print;

	DecimalFormat df = new DecimalFormat("###0.00");

	String voucher_code = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voucher_history_page_view);

		tv_voucher_code = (TextView) this.findViewById(R.id.text_voucher_history_page_code);
		tv_buyer_name = (TextView) this.findViewById(R.id.text_voucher_history_page_buyer_name);
		tv_voucher_redeem_date = (TextView) this.findViewById(R.id.text_voucher_history_page_date_redeemed);
		tv_title = (TextView) this.findViewById(R.id.text_voucher_history_page_option);
		tv_price = (TextView) this.findViewById(R.id.text_voucher_history_page_price);
		tv_value = (TextView) this.findViewById(R.id.text_voucher_history_page_value);
		tv_purchased_date = (TextView) this.findViewById(R.id.text_voucher_history_page_date_purchased);
		tv_fine_print = (TextView) this.findViewById(R.id.text_voucher_history_page_fine_print);

		btn_vhp_delete = (Button) this.findViewById(R.id.btn_voucher_history_page_delete);
		btn_vhp_back = (Button) this.findViewById(R.id.btn_voucher_history_page_back);

		btn_vhp_delete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ad_delete_confirm.create().show();
			}
		});

		btn_vhp_back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(VoucherHistoryPageView.this, VoucherHistoryListView.class);
				startActivity(intent);
				finish();
			}
		});

		try {
			Bundle extrasBundle = getIntent().getExtras();
			voucher_code = extrasBundle.getString(KEY_VOUCHER_HISTORY_PAGE_CODE);
			tv_voucher_code.setText("#" + voucher_code);
		} catch (Exception e) {
			tv_voucher_code.setText("Can't find Voucher Code");
		}

		try {
			refreshVoucherData(voucher_code);

		} catch (Exception e) {
			tv_voucher_code.setVisibility(View.GONE);
			tv_buyer_name.setVisibility(View.GONE);
			tv_voucher_redeem_date.setVisibility(View.GONE);
			tv_title.setVisibility(View.GONE);
			tv_price.setVisibility(View.GONE);
			tv_value.setVisibility(View.GONE);
			tv_purchased_date.setVisibility(View.GONE);
			tv_fine_print.setVisibility(View.GONE);
		}

		ad_delete_confirm = new AlertDialog.Builder(this);
		ad_delete_confirm.setTitle("Delete Confirm");
		ad_delete_confirm.setMessage("Delete this record?");
		ad_delete_confirm.setCancelable(true);

		ad_delete_confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				deleteVoucherHistory();

				Intent intent = new Intent();
				intent.setClass(VoucherHistoryPageView.this, VoucherHistoryListView.class);
				startActivity(intent);
				finish();

			}
		});

		ad_delete_confirm.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			}
		});

	}

	private void deleteVoucherHistory() {

		try {
			VoucherHistoryPageUtils.deleteExternalStoragePrivateFile(this, voucher_code);
			Toast.makeText(VoucherHistoryPageView.this, voucher_code + " has deleted.", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {

		}
	}

	/*
	 * JSON Parser, Voucher Data
	 */
	private void refreshVoucherData(String barcode) {

		String fname = barcode;
		final byte[] buffer;
		buffer = readExternallStoragePrivate(fname);
		VoucherInfoUtils.deleteInternalStoragePrivate(this, fname);

		if (buffer != null) {
			new Thread() {
				@Override
				public void run() {
					try {
						String voucher_data_string = new String(buffer);
						final JSONObject json_object_voucher_data = new JSONObject(voucher_data_string);
						final JSONObject json_object_voucher_data_0 = json_object_voucher_data.getJSONObject("Data");

						final double price = json_object_voucher_data_0.getDouble("Price");
						final double value = json_object_voucher_data_0.getDouble("Value");
						final String buyer_first_name = json_object_voucher_data_0.getString("BuyerFirstName");
						final String buyer_last_name = json_object_voucher_data_0.getString("BuyerLastName");
						final String option_title = json_object_voucher_data_0.getString("DealOptionTitle");
						final String date_purchase = json_object_voucher_data_0.getString("DatePurchased");
						// final String date_redeemed = json_object_voucher_data_0.getString("DateRedeemed");
						final String fine_print = json_object_voucher_data_0.getString("DealOptionFinePrint");

						VoucherHistoryPageView.this.runOnUiThread(new Runnable() {
							public void run() {
								tv_buyer_name.setText(buyer_first_name + " " + buyer_last_name);
								tv_purchased_date.setText(date_purchase);

								try {
									tv_price.setText("$" + df.format(price));
									tv_value.setText("$" + df.format(value));
								} catch (Exception e) {
									tv_price.setText("$" + Double.toString(price));
									tv_value.setText("$" + Double.toString(value));
								}

								tv_title.setText(option_title);
								tv_fine_print.setText(HtmlUtils.convertHtmltoString(fine_print));

							}
						});

					} catch (Exception e) {

					}
				}
			}.start();
		} else {

		}
	}

	/*
	 * Read Voucher from SD Card
	 */
	public byte[] readExternallStoragePrivate(String filename) {
		int len = 1024 * 256;
		byte[] buffer = new byte[len];
		if (!ExternalStorageUtils.isExternalStorageReadOnly()) {
			try {
				File file = new File(getExternalFilesDir("voucherhistory"), filename);
				FileInputStream fis = new FileInputStream(file);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int nrb = fis.read(buffer, 0, len);
				while (nrb != -1) {
					baos.write(buffer, 0, nrb);
					nrb = fis.read(buffer, 0, len);
				}
				buffer = baos.toByteArray();
				fis.close();
			} catch (FileNotFoundException e) {
				Log.d(appContext.getString(R.string.app_name) + ".readInternalStorage()", "FileNotFoundException: " + e);
				e.printStackTrace();
			} catch (IOException e) {
				Log.d(appContext.getString(R.string.app_name) + ".readInternalStorage()", "IOException: " + e);
				e.printStackTrace();
			} catch (Exception e) {

			}
		}
		return buffer;
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(VoucherHistoryPageView.this, VoucherHistoryListView.class);
		startActivity(intent);
		finish();
	}

}
