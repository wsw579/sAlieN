document.addEventListener('DOMContentLoaded', function () {
    // Simple-DataTables
    // https://github.com/fiduswriter/Simple-DataTables/wiki

    const datatablesSimple = document.getElementById('datatablesSimple');
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple,
        {
            serverSide: true, // 서버 사이드 처리 활성화
            paging: false, // 페이지네이션 비활성화
            searching: false, // 검색 활성화 (필요에 따라 설정)
            ordering: false, // 정렬 활성화 (필요에 따라 설정)
            info: false, // 테이블 정보 숨기기 (하단 정보 텍스트 비활성화)
        });

        // 검색창 제거
                const searchInput = document.querySelector('.datatable-input');
                        if (searchInput) {
                            searchInput.remove(); // 검색창 DOM에서 삭제
                        }
    }
});

//document.addEventListener('DOMContentLoaded', function () {
//    const datatablesSimple = document.getElementById('datatablesSimple');
//    if (datatablesSimple) {
//        new simpleDatatables.DataTable(datatablesSimple, {
//            serverSide: true, // 서버 사이드 처리 활성화
//            paging: false, // 페이지네이션 비활성화
//            searching: false, // 검색 활성화 (필요에 따라 설정)
//            ordering: false, // 정렬 활성화 (필요에 따라 설정)
//            info: false, // 테이블 정보 숨기기 (하단 정보 텍스트 비활성화)
//            ajax: (data, callback) => {
//                const page = Math.floor(data.start / data.length); // 현재 페이지 번호 계산
//                const size = data.length; // 페이지 크기
//
//                // 서버에서 데이터 가져오기
//                fetch(`/orders?page=${page}&size=${size}&search=${data.search.value || ''}&sortColumn=${data.order[0]?.column || 'orderDate'}&sortDirection=${data.order[0]?.dir || 'asc'}`)
//                                    .then(response => response.json())
//                                    .then(result => {
//                                        callback({
//                                            data: result.data,
//                                            recordsTotal: result.recordsTotal,
//                                            recordsFiltered: result.recordsFiltered,
//                                        });
//                                    })
//                                    .catch(error => console.error("Error loading data:", error));
//                            },
//        });
//    }
//});
//
