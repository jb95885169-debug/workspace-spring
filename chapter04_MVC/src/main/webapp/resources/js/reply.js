console.log('reply module....');

const replyService = (function(){
	
	// 댓글 등록
	function add(reply, callback){
		fetch('/reply/new', {
				method : 'post',
				body : JSON.stringify(reply),
				headers : {'Content-type' : 'application/json; charset=utf-8'}
			})
			.then(response => response.text())
			.then(data => {
				callback(data);
			})
			.catch(err => console.log(err));
	}
	
	// 댓글 전체 조회
	function getList(bno, callback){
		fetch('/reply/pages/' + bno + ".json")
		.then(response => response.json())
		.then(data => {
			callback(data);
		})
		.catch(err => console.log(err));
	}
	
	// 댓글 삭제
	function remove(rno, callback){
		fetch('/reply/' + rno, {method : 'delete'})
		.then(response => response.text())
		.then(data => {
			callback(data);
		})
		.catch(err => console.log(err));
	}
	// 댓글 수정
	function update(reply, callback){
		fetch('/reply/' + reply.rno, {
			method : 'put',
			body : JSON.stringify(reply),
			headers : {'Content-type' : 'application/json; charset=utf-8'}
		})
		.then(response => response.text())
		.then(data => {
			callback(data);
		})
		.catch(err => console.log(err));
	}
	// 댓글 조회
	function get(rno, callback){
		fetch('/reply/' + rno + ".json")
		.then(response => response.json())
		.then(data => {
			callback(data);
		})
		.catch(err => console.log(err));
	}
	return {
		add : add,
		getList : getList,
		remove : remove,
		update : update,
		get : get
	};
	
})();





