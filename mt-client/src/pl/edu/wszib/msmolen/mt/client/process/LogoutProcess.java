package pl.edu.wszib.msmolen.mt.client.process;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import pl.edu.wszib.msmolen.mt.client.utils.UserManager;
import pl.edu.wszib.msmolen.mt.common.exchange.Const;

public class LogoutProcess extends AbstractProcess
{
	private final String mUserName;

	public LogoutProcess(String pmUserName)
	{
		mUserName = pmUserName;
	}

	@Override
	protected URL getURL() throws Exception
	{
		return new URL("https://localhost:8443/mt-server/exchange/getUser");
	}

	@Override
	protected void sendRequest(ObjectOutputStream pmOutput) throws IOException
	{
		pmOutput.writeObject(Const.MODE_LOGOUT);
		pmOutput.writeObject(mUserName);
	}

	@Override
	protected void processResponse(ObjectInputStream pmInput) throws Exception
	{
		String lvResponse = (String) pmInput.readObject();
		if (Const.MESSAGE_OK.equals(lvResponse))
		{
			UserManager.getInstance().setUser(null);
		}
		else
		{
			throw (Exception) pmInput.readObject();
		}
	}

}
