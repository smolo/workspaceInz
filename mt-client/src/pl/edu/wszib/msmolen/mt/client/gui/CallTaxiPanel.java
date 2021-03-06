package pl.edu.wszib.msmolen.mt.client.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pl.edu.wszib.msmolen.mt.client.process.LogoutProcess;
import pl.edu.wszib.msmolen.mt.client.process.OrderTaxiProcess;
import pl.edu.wszib.msmolen.mt.client.process.WaitForTaxiProcess;
import pl.edu.wszib.msmolen.mt.client.utils.UserManager;
import pl.edu.wszib.msmolen.mt.common.auth.User;
import pl.edu.wszib.msmolen.mt.common.exchange.Const;

public class CallTaxiPanel extends JPanel
{

	private static final long serialVersionUID = -3366954965931444343L;

	private final JLayeredPane mParent;

	private final JButton mBackButton;

	private final JTextField mPassengersCount;
	private final JCheckBox mLuggageTaxi;
	private final JCheckBox mCombiTaxiCheckBox;

	private final JButton mChooseLocationButton;
	private final JButton mCheckPriceButton;
	private final JButton mCheckTimeButton;
	private final JButton mCallTaxiButton;

	private final ActionListener listener;

	private double[] mCoordinates;
	private int mOrderId;

	public CallTaxiPanel(JLayeredPane pmParent)
	{
		mParent = pmParent;

		this.setLayout(null);
		this.setSize(mParent.getWidth() - 15, mParent.getHeight() - 45);
		this.setLocation(5, 5);
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		listener = new ButtonActionListener(this);

		mBackButton = new JButton(UserManager.getInstance().getUser() != null ? "Wyloguj" : "< Wstecz");
		mBackButton.setSize(100, 30);
		mBackButton.setLocation(getWidth() - 100, 0);
		mBackButton.addActionListener(listener);
		this.add(mBackButton);

		mPassengersCount = new JTextField("Liczba pasa�er�w");
		mPassengersCount.setSize(200, 35);
		mPassengersCount.setLocation(5, 40);
		mPassengersCount.addFocusListener(new FocusAdapter()
		{
			@Override
			public void focusGained(FocusEvent arg0)
			{
				try
				{
					Integer.parseInt(mPassengersCount.getText());
				}
				catch (Exception e)
				{
					mPassengersCount.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent arg0)
			{
				if ("".equals(mPassengersCount.getText().trim()))
					mPassengersCount.setText("Liczba pasa�er�w");
			}
		});
		this.add(mPassengersCount);

		mCombiTaxiCheckBox = new JCheckBox("Du�y baga�nik");
		mCombiTaxiCheckBox.setSize(200, 20);
		mCombiTaxiCheckBox.setLocation(5, 80);
		this.add(mCombiTaxiCheckBox);

		mLuggageTaxi = new JCheckBox("Taxi baga�owe");
		mLuggageTaxi.setSize(200, 20);
		mLuggageTaxi.setLocation(5, 105);
		this.add(mLuggageTaxi);

		mChooseLocationButton = new JButton("Wska� lokalizacj�");
		mChooseLocationButton.setSize(200, 30);
		mChooseLocationButton.setLocation(5, 140);
		mChooseLocationButton.addActionListener(listener);
		this.add(mChooseLocationButton);

		mCheckPriceButton = new JButton("Sprawd� cen�");
		mCheckPriceButton.setSize(200, 30);
		mCheckPriceButton.setLocation(5, 175);
		mCheckPriceButton.addActionListener(listener);
		mCheckPriceButton.setEnabled(false);
		this.add(mCheckPriceButton);

		mCheckTimeButton = new JButton("Sprawd� czas oczekiwania");
		mCheckTimeButton.setSize(200, 30);
		mCheckTimeButton.setLocation(5, 210);
		mCheckTimeButton.addActionListener(listener);
		mCheckTimeButton.setEnabled(false);
		this.add(mCheckTimeButton);

		mCallTaxiButton = new JButton("Zam�w taks�wk�");
		mCallTaxiButton.setSize(200, 30);
		mCallTaxiButton.setLocation(5, 245);
		mCallTaxiButton.addActionListener(listener);
		mCallTaxiButton.setEnabled(false);
		this.add(mCallTaxiButton);

	}

	private class ButtonActionListener implements ActionListener
	{
		private final JPanel mPanel;

		public ButtonActionListener(JPanel pmPanel)
		{
			mPanel = pmPanel;
		}

		@Override
		public void actionPerformed(ActionEvent evt)
		{
			if (mBackButton.equals(evt.getSource()))
			{
				User lvUser = UserManager.getInstance().getUser();
				if (lvUser != null)
				{
					new LogoutProcess(UserManager.getInstance().getUser().getName()).process();
				}

				mParent.moveToBack(mPanel);
			}

			else if (mChooseLocationButton.equals(evt.getSource()))
			{
				final ChooseLocationDialog lvDialog = new ChooseLocationDialog((StartWindow) mParent.getRootPane().getParent());
				lvDialog.setCoordinates(mCoordinates);
				lvDialog.addWindowListener(new WindowAdapter()
				{
					@Override
					public void windowClosed(WindowEvent e)
					{
						if (lvDialog.getCoordinates() != null)
						{
							mCoordinates = lvDialog.getCoordinates();
							mChooseLocationButton.setText("Zmie� lokalizacj�");
							mCallTaxiButton.setEnabled(true);
							mCheckPriceButton.setEnabled(true);
							mCheckTimeButton.setEnabled(true);
						}
						else
						{
							if (mCoordinates == null)
							{
								mChooseLocationButton.setText("Wska� lokalizacj�");
								mCallTaxiButton.setEnabled(false);
								mCheckPriceButton.setEnabled(false);
								mCheckTimeButton.setEnabled(false);
							}
						}
					}

				});

				lvDialog.setVisible(true);
			}
			else if (mCheckTimeButton.equals(evt.getSource()))
			{
				new OrderTaxiProcess(mCoordinates[0], mCoordinates[1], Const.ORDER_OP_ASK_FOR_TIME).process();
			}
			else if (mCallTaxiButton.equals(evt.getSource()))
			{
				int lvWynik = JOptionPane.showOptionDialog(mPanel, "Czy na pewno chcesz zam�wi� taks�wk�?", "Potwierdzenie zam�wienia",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Tak", "Nie" }, "Tak");

				if (lvWynik == 0)
				{
					OrderTaxiProcess process = new OrderTaxiProcess(mCoordinates[0], mCoordinates[1], Const.ORDER_OP_ORDER);
					process.process();
					mOrderId = process.getOrderId();

					Timer lvTimer = new Timer();
					lvTimer.scheduleAtFixedRate(new TimerTask()
					{
						@Override
						public void run()
						{
							WaitForTaxiProcess process = new WaitForTaxiProcess(mOrderId);
							process.process();
							if ("2".equals(process.getOrderStatus()))
							{
								JOptionPane.showMessageDialog(mPanel, "Taks�wka przyjecha�a. Wska� miejsce docelowe.");
								this.cancel();
							}
						}
					}, 0, 5 * 1000);
				}
			}
		}
	}

}
