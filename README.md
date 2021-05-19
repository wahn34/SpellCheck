
# SpellCheck

언택트 시대, 직접 만나서 얘기하는 시간보다 문자로 대화하는 시간이 많아졌습니다.  
책 이외의 컨텐츠 이용 시간이 늘어남에 따라 맞춤법 검사기의 사용량이 늘고 있습니다.  


SpellCheck는 빠르게 사용할 수 있는 맞춤법 검사 Android Application입니다.  
누구나 **쉽고**, **빠르게**, **정확하게** 사용할 수 있도록 하자는 목표로 탄생했습니다.  

SpellCheck는  [네이버 맞춤법검사기][naverlink]의 데이터로 제공됩니다.

[naverlink]: https://search.naver.com/search.naver?where=nexearch&sm=top_sug.pre&fbm=0&acr=1&acq=%EB%84%A4%EC%9D%B4%EB%B2%84%EB%A7%9E%EC%B6%A4%EB%B2%95&qdt=0&ie=utf8&query=%EB%84%A4%EC%9D%B4%EB%B2%84+%EB%A7%9E%EC%B6%A4%EB%B2%95+%EA%B2%80%EC%82%AC%EA%B8%B0 "Go naver"

### 쉬운 사용
```
  띄어쓰기 교정
  단어 맞춤법 교정
  클립보드 복사
```

### 빠른 사용
```
  틀렸던 단어 즉시 교정
  터치하여 바로 교정
  알림 영역에서 터치하여 팝업 실행
```

### 정확한 사용
```
  직접 터치하여 원하는 부분만 수정
  교정 대상, 결과 확인
  교정된 이유 확인
```
<img src="/images/main.jpg" width="180px" height="370px" title="메인" alt="main"></img>
<img src="/images/sit2.jpg" width="180px" height="370px" title="사용2" alt="situation2"></img>
<img src="/images/history1.jpg" width="180px" height="370px" title="기록1" alt="history1"></img>
<img src="/images/pop.jpg" width="180px" height="370px" title="팝업1" alt="pop"></img>
<br/>


# 사용 라이브러리
https://github.com/blackfizz/EazeGraph

# 수정 필요
사용자 데이터는 내부 SQLite와 AWS EC2 환경의 Oracle DB에 저장되도록 설정되어있습니다.  
이 부분을 수정하거나 주석 후 사용해야 합니다.
```java
@Override
protected ArrayList<String> doInBackground( String... params){
	wrdLst.clear();
	ResultSet reset = null;
	Connection conn = null;
	try {
		Class.forName("oracle.jdbc.driver.OracleDriver");
		conn = DriverManager.getConnection("jdbc:oracle:thin:@orclelec.cfvmyazpemfk.us-east-1.rds.amazonaws.com:1521:orcl","rywn34","myelectric");
		Statement stmt = conn.createStatement();
		reset = stmt.executeQuery(query);
		while(reset.next()){
			if ( isCancelled() ) break;
			final String str = reset.getString(1)+"<1>"+reset.getString(2);
			wrdLst.add(str);
		}
	conn.close();
	}
	catch (Exception e) {}
	return wrdLst;
}
```

# Changelog

#### 1.0.0
 + initial commit
#### 1.0.1
 + 불필요한 이미지 제거
