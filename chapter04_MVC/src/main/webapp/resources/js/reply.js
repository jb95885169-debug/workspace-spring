console.log('reply module...');


const replyService = (function(){

    // 댓글 등록 
    function add(reply, callback){
    	fetch('/reply/new',{
    		method : 'post',
			body : JSON.stringify(reply),	
			headers : {'Content-type' : 'application/json; charset=utf-8'}
    	})
    	.then(response => response.text())// 객체를 받으면 .json ,  순수 문자열을 받으면 .text
    	.then(data => {
    		callback(data);
    	})
    	.catch(err => console.log(err));
    }
    
    // 댓글 전체  조회
    function getList(bno, callback) {
    	fetch('/reply/pages/' + bno + ".json")
    	.then(response => response.json())// 객체를 받으면 .json ,  순수 문자열을 받으면 .text
    	.then(data => {
    		callback(data);
    	})
    	.catch(err => console.log(err));
	}
    
    // 댓글 삭제
    function remove(rno, callback) {
		
	}
    // 댓글 수정
    function update(reply, callback) {
		
	}
    // 댓글 조회
    function get(rno, callback) {
		
	}
    
    
    
    
    return{
        add : add,
        getList : getList,
        remove : remove,
        update : update,
        get : get,
    };

})();





























