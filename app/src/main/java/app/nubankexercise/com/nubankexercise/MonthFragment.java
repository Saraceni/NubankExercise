package app.nubankexercise.com.nubankexercise;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import app.nubankexercise.com.nubankexercise.adapter.LineItemsAdapter;

/**
 * Created by rafaelgontijo on 6/25/15.
 */
public class MonthFragment extends Fragment {

    public static final int OVERDUE_STATE = 33; // Show Pagamento Recebido if paid is negative
    public static final int CLOSED_STATE = 22;
    public static final int OPEN_STATE = 11;
    public static final int FUTURE_STATE = 44;

    public static final String TYPE_KEY = "app.nubankexercise.com.nubankexercise.TYPE_KEY";

    private TextView tvTotalBalance;
    private TextView tvDueDateHeader;
    private TextView tvCloseDateHeader;
    private Button button;
    private TextView tvPagtoRecebidoText;
    private TextView tvOverduePaid;
    private TextView tvTotalCumulative;
    private TextView tvPastBalance;
    private TextView tvPagtoText;
    private TextView tvInterest;
    private TextView tvPastBalanceText;
    private TextView tvInterestText;
    private TextView tvDateInterval;

    private LinearLayout containerFaturaPaga;
    private RelativeLayout containerFaturaFechada;
    private FrameLayout containerButton;
    private RelativeLayout mainHeader;

    private ListView listView;

    private int state;
    private JSONObject bill;
    private JSONObject summary;
    private JSONArray lineItems;

    public static MonthFragment newInstance(JSONObject bill)
    {
        MonthFragment fragment = new MonthFragment();
        fragment.setBillType(bill);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void setBillType(JSONObject bill)
    {
        this.bill = bill;
        setTypeID();
        try
        {
            lineItems = bill.getJSONArray("line_items");
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void setTypeID()
    {
        try {
            String billType = bill.getString("state");
            summary = bill.getJSONObject("summary");
            Log.i("APP", "Bill type = " + billType);
            if(billType.contains("overdue"))
            {
                state = OVERDUE_STATE;
            }
            else if(billType.contains("closed"))
            {
                state = CLOSED_STATE;
            }
            else if(billType.contains("open"))
            {
                state = OPEN_STATE;
            }
            else if(billType.contains("future"))
            {
                state = FUTURE_STATE;
            }
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void setHeader()
    {
        try {
            String totalBalance = summary.getString("total_balance");
            String formattedBalance = returnFormattedPrice(totalBalance);
            tvTotalBalance.setText(formattedBalance);
            String dueDate = summary.getString("due_date");
            Log.i("APP", "date = " + dueDate);
            String[] date = returnFormattedDate(dueDate);
            Log.i("APP", "formatted date = " + date[0] + " de " + date[1]);
            tvDueDateHeader.setText("Vencimento " + date[0] + " de " + date[1].substring(0,3));
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    public static String[] returnFormattedDate(String toFormat)
    {
        String res[] = new String[2];
        res[0] = toFormat.substring(toFormat.length() - 2);
        String str = toFormat.substring(5,7);
        Log.i("APP", str);
        switch(str)
        {
            case "01":
                res[1] = "JANEIRO";
                break;
            case "02":
                res[1] = "FEVEREIRO";
                break;
            case "03":
                res[1] = "MARCO";
                break;
            case "04":
                res[1] = "ABRIL";
                break;
            case "05":
                res[1] = "MAIO";
                break;
            case "06":
                res[1] = "JUNHO";
                break;
            case "07":
                res[1] = "JULHO";
                break;
            case "08":
                res[1] = "AGOSTO";
                break;
            case "09":
                res[1] = "SETEMBRO";
                break;
            case "10":
                res[1] = "OUTUBRO";
                break;
            case "11":
                res[1] = "NOVEMBRO";
                break;
            case "12":
                res[1] = "DEZEMBRO";
                break;
        }

        return res;

    }


    public static String returnFormattedPrice(String toFormat)
    {
        String res;
        if(toFormat.length() > 2) {
            String str = toFormat.substring(0, toFormat.length() - 2) + "." + toFormat.substring(toFormat.length() - 2, toFormat.length());
            Float f_res = Float.parseFloat(str);
            Locale ptBr = new Locale("pt", "BR");
            NumberFormat numberFormat = NumberFormat.getNumberInstance(ptBr);
            res = "R$ " + numberFormat.format(f_res);
            if(!res.contains(","))
            {
                res = res + ",00";
            }
            else if(res.indexOf(",") == res.length() - 2 )
            {
                res = res + "0";
            }
        }
        else if(toFormat.length() == 2)
        {
            res = "R$ 0," + toFormat;
        }
        else
        {
            res = "R$ 0,0" + toFormat;
        }
        return res;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.month_fragment, null);
        tvTotalBalance = (TextView) v.findViewById(R.id.textview_total_balance);
        tvDueDateHeader = (TextView) v.findViewById(R.id.textview_due_date_header);
        tvCloseDateHeader = (TextView) v.findViewById(R.id.textview_close_date_header);
        button = (Button) v.findViewById(R.id.button_gerar_boleto);

        setHeader();

        mainHeader = (RelativeLayout) v.findViewById(R.id.colorfull_header_container);
        containerFaturaPaga = (LinearLayout) v.findViewById(R.id.container_fatura_paga);
        containerFaturaFechada = (RelativeLayout) v.findViewById(R.id.container_fatura_fecahda);
        containerButton = (FrameLayout) v.findViewById(R.id.container_button);

        tvPagtoRecebidoText = (TextView) v.findViewById(R.id.tv_pagto_recebido);
        tvOverduePaid = (TextView) v.findViewById(R.id.text_view_paid);
        tvTotalCumulative = (TextView) v.findViewById(R.id.textview_total_cumulative);
        tvPastBalance = (TextView) v.findViewById(R.id.textview_past_balance);
        tvPagtoText = (TextView) v.findViewById(R.id.access_valores_nao_pagos);
        tvInterest = (TextView) v.findViewById(R.id.textview_interest);
        tvInterestText = (TextView) v.findViewById(R.id.access_juros);
        tvPastBalanceText = (TextView) v.findViewById(R.id.access_valores_nao_pagos);
        tvDateInterval = (TextView) v.findViewById(R.id.textview_date_interval);
        try {
            String openDate = summary.getString("open_date");
            String closeDate = summary.getString("close_date");
            String formatOpenDate[] = MonthFragment.returnFormattedDate(openDate);
            String formatCloseDate[] = MonthFragment.returnFormattedDate(closeDate);
            tvDateInterval.setText("DE " + formatOpenDate[0] + " " + formatOpenDate[1].substring(0,3)
            + " ATÉ " + formatCloseDate[0] + " " + formatCloseDate[1].substring(0,3));

        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }

        listView = (ListView) v.findViewById(R.id.listview);
        LineItemsAdapter adapter = new LineItemsAdapter(getActivity(), lineItems);
        listView.setAdapter(adapter);

        switch(state)
        {
            case OPEN_STATE:
                setOpenState();
                break;
            case OVERDUE_STATE:
                setOverdueState();
                break;
            case CLOSED_STATE:
                setClosedState();
                break;
            case FUTURE_STATE:
                setFutureState();
                break;
            default:
                break;
        }


        return v;
    }

    private void setOpenState()
    {
        int mainColor = getResources().getColor(R.color.color_open);
        tvCloseDateHeader.setVisibility(View.VISIBLE);
        containerFaturaPaga.setVisibility(View.GONE);
        containerFaturaFechada.setVisibility(View.GONE);
        containerButton.setVisibility(View.VISIBLE);
        mainHeader.setBackgroundColor(mainColor);
        button.setBackground(getResources().getDrawable(R.drawable.blue_border_button));
        button.setTextColor(mainColor);
        try
        {
            String closedDate = summary.getString("close_date");
            String[] date = returnFormattedDate(closedDate);
            tvCloseDateHeader.setText("FECHAMENTO EM " + date[0] + " DE " + date[1]);
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void setOverdueState()
    {
        int mainColor = getResources().getColor(R.color.color_overdue);
        tvCloseDateHeader.setVisibility(View.GONE);
        containerFaturaPaga.setVisibility(View.VISIBLE);
        containerFaturaFechada.setVisibility(View.GONE);
        containerButton.setVisibility(View.GONE);
        mainHeader.setBackgroundColor(mainColor);
        button.setBackground(getResources().getDrawable(R.drawable.red_border_button));
        button.setTextColor(mainColor);
        try
        {
            String paid = summary.getString("paid");
            if(paid.charAt(0) == '-')
            {
                tvPagtoRecebidoText.setVisibility(View.VISIBLE);
                paid = paid.substring(1);
            }
            else
            {
                tvPagtoRecebidoText.setVisibility(View.GONE);
            }
            String formattedPaid = returnFormattedPrice(paid);
            tvOverduePaid.setText(formattedPaid);


        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void setClosedState()
    {
        int mainColor = getResources().getColor(R.color.color_closed);
        tvCloseDateHeader.setVisibility(View.GONE);
        containerFaturaPaga.setVisibility(View.GONE);
        containerFaturaFechada.setVisibility(View.VISIBLE);
        containerButton.setVisibility(View.VISIBLE);
        mainHeader.setBackgroundColor(mainColor);
        try
        {
            String total_cumulative = summary.getString("total_cumulative");
            if(!total_cumulative.contains("-") && !total_cumulative.equals("0"))
            {
                String totalFormated = returnFormattedPrice(total_cumulative);
                tvTotalCumulative.setText(totalFormated);
            }
            else
            {
                tvTotalCumulative.setText("");
                tvPagtoText.setText("");
            }
            String pastBalance = summary.getString("past_balance");
            if(!pastBalance.contains("-") && !pastBalance.equals("0"))
            {
                if(pastBalance.contains("-"))
                {
                    pastBalance = pastBalance.substring(0);
                }
                tvPagtoText.setText("Valores não pagos");
                String pastBalanceFormatted = returnFormattedPrice(pastBalance);
                tvPastBalance.setText(pastBalanceFormatted);
            }
            else if(!pastBalance.equals("0"))
            {
                tvPagtoText.setText("Valores pré-pago");
                String pastBalanceFormatted = returnFormattedPrice(pastBalance);
                tvPastBalance.setText(pastBalanceFormatted);
            }
            else
            {
                tvPagtoText.setText("");
                tvPastBalance.setText("");
            }
            String interest = summary.getString("interest");
            if(!interest.contains("-") && !interest.equals("0"))
            {
                String interestFormatted = returnFormattedPrice(interest);
                tvInterest.setText(interestFormatted);
            }
            else
            {
                tvInterestText.setText("");
                tvInterest.setText("");
            }

        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }
    }

    private void setFutureState()
    {
        int mainColor = getResources().getColor(R.color.color_future);
        tvCloseDateHeader.setVisibility(View.VISIBLE);
        containerFaturaPaga.setVisibility(View.GONE);
        containerFaturaFechada.setVisibility(View.GONE);
        containerButton.setVisibility(View.GONE);
        mainHeader.setBackgroundColor(mainColor);
        tvCloseDateHeader.setText("FATURA PARCIAL");
    }







}

