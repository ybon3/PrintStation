> # 限制 #
>
> 簡單來說，沒照規矩來系統就是掛


* Windows OS 限定，因為目前底層是使用 `wmic` 指令取得印表機資訊。
* 印表機名稱不可有中文，或者說只允許有 ANSI 的符號，否則 `wmic` 指定的 output 會拆解錯誤。
* POST API 有傳輸限制，Tomcat 的預設值為 2,097,152 (2 megabytes)，
	調整範例（修改 `conf/server.xml`，增加 `maxPostSize` 設定值）：
	```xml
	<Connector port="8080" protocol="HTTP/1.1"
	           connectionTimeout="20000"
	           redirectPort="8443"
	           maxPostSize="1073741824" />
	```
