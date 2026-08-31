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







