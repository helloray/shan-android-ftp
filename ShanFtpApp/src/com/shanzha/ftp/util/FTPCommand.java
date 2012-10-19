package com.shanzha.ftp.util;

/**
 * Ftp命令常量类
 * 
 * @author ShanZha
 * @date 2012-9-26 11:00
 * 
 */
public abstract class FTPCommand {

	public static final int USER = 0;
	public static final int PASS = 1;
	public static final int ACCT = 2;
	public static final int CWD = 3;
	public static final int CDUP = 4;
	public static final int SMNT = 5;
	public static final int REIN = 6;
	public static final int QUIT = 7;
	public static final int PORT = 8;
	public static final int PASV = 9;
	public static final int TYPE = 10;
	public static final int STRU = 11;
	public static final int MODE = 12;
	public static final int RETR = 13;
	public static final int STOR = 14;
	public static final int STOU = 15;
	public static final int APPE = 16;
	public static final int ALLO = 17;
	public static final int REST = 18;
	public static final int RNFR = 19;
	public static final int RNTO = 20;
	public static final int ABOR = 21;
	public static final int DELE = 22;
	public static final int RMD = 23;
	public static final int MKD = 24;
	public static final int PWD = 25;
	public static final int LIST = 26;
	public static final int NLST = 27;
	public static final int SITE = 28;
	public static final int SYST = 29;
	public static final int STAT = 30;
	public static final int HELP = 31;
	public static final int NOOP = 32;

	/**
	 * 不能实例化
	 */
	private FTPCommand() {
	}

	private static final String[] _commands = { "USER", "PASS", "ACCT", "CWD",
			"CDUP", "SMNT", "REIN", "QUIT", "PORT", "PASV", "TYPE", "STRU",
			"MODE", "RETR", "STOR", "STOU", "APPE", "ALLO", "REST", "RNFR",
			"RNTO", "ABOR", "DELE", "RMD", "MKD", "PWD", "LIST", "NLST",
			"SITE", "SYST", "STAT", "HELP", "NOOP" };

	/**
	 * 提供命令给外界
	 * 
	 * @param command
	 * @return
	 */
	public static String getCommand(int command) {
		return _commands[command];
	}
}
