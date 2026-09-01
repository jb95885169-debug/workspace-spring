document.querySelectorAll('.header a').forEach(a=>{
	a.addEventListener('click', e=>{
		e.preventDefault();
		
		let menu = e.target.getAttribute('href');
		
		console.log(menu);

		if(menu === 'mainPage'){
			location.href = '/';
		}else if(menu === 'boardList'){
			location.href = '/board/list';
		}

	});
});

// 전역에서 페이징을 사용하기 위한 함수 작성
function setStorageData(pageNum, amount){
	const pageData = {
		pageNum : pageNum,
		amount : amount
	};
	localStorage.setItem('page_data', JSON.stringify(pageData));// JSON.stringify(pageData)어떠한 데이터를 제이슨으로 바꿔주는코드 
}

function getStorageData(){
	return JSON.parse( localStorage.getItem('page_data'));	// 
}











