package com.ditp.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ditp.service.wallet.WalletPlatFromService;
import com.wallet.platfrom.filter.AbstractWalletPlatfromFilter;
import com.wallet.platfrom.sdk.IWalletPlatfromInterface;

@Component
public class WalletPlatFromFilter extends AbstractWalletPlatfromFilter {

	@Autowired
	private WalletPlatFromService walletPlatFromService;

	@Override
	public IWalletPlatfromInterface getWalletPlatfromInterface() {
		// TODO Auto-generated method stub
		// walletPlatFromService.getWithdrawDatas();
		return walletPlatFromService;
	}

}
