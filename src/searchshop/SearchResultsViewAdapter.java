package searchshop;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.Header;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.l3cache.snapshop.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;

public class SearchResultsViewAdapter extends ArrayAdapter<SearchResultsData> {
	private Context mContext;
	private int mLayoutResourceId;
	private ArrayList<SearchResultsData> mResultData;

	public SearchResultsViewAdapter(Context context, int layoutResourceId, ArrayList<SearchResultsData> resultData) {
		super(context, layoutResourceId, resultData);
		mContext = context;
		mLayoutResourceId = layoutResourceId;
		mResultData = resultData;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;

		if (row == null) {
			LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
			row = inflater.inflate(mLayoutResourceId, parent, false);
		}

		final ImageView itemImageView = (ImageView) row.findViewById(R.id.searchresults_list_row_image_view);
		TextView itemNameTextView = (TextView) row.findViewById(R.id.searchresults_list_row_item_name_text_view);
		TextView itemPriceTextView = (TextView) row.findViewById(R.id.searchresults_list_row_itemPrice_text_view);

		itemNameTextView.setText(Html.fromHtml(mResultData.get(position).getTitle()));
		String priceRange = mResultData.get(position).getLprice() + "원 ~ " + mResultData.get(position).getHprice() + "원";
		itemPriceTextView.setText(priceRange);

		try {
			AsyncHttpClient client = new AsyncHttpClient();
			client.get(mResultData.get(position).getImage(), new FileAsyncHttpResponseHandler(mContext) {

				@Override
				public void onFailure(int statusCode, Header[] headers, Throwable throwable, File response) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onSuccess(int statusCode, Header[] headers, File response) {
					Log.i("Search", response + "");
					Drawable d = Drawable.createFromPath(response.toString());
					itemImageView.setImageDrawable(d);
				}
			});

		} catch (Exception e) {
			// TODO: handle exception
		}

		return row;
	}
}
