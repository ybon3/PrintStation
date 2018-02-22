假設網站的 ROOT 為：http://localhost/PrintStation，
下列 API 以此為相對路徑。

- HTTP POST 打印請求
	- API：`/print`
	- POST 參數：
		- pn：印表機名稱
		- b64：列印檔案以 base64 編碼的字串
		- type：列印檔案的 MIME Type
	- 回傳內容為 json 格式：
		- `stat`：
			- 1：列印成功
			- 0：列印失敗（任何未定義的錯誤）
			- -1：POST 參數錯誤（pn 沒給、b64 與 type 數量不一致 ... etc）
			- -2：指定的印表機不存在或無法使用
			- -3：不支援的 MIME Type
			- -4：檔案上傳 IO 錯誤
		- `msg`：錯誤訊息 or 交易序號