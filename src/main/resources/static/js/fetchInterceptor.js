// fetchInterceptor.js

// 인증, 인가가 필요 없는 API 엔드포인트 목록
const WHITER_LIST = [
    "/error/**", "/css/**", "/js/**", "/",
    "/loginForm", "/registerForm", //USER VIEW
    "/login", "/sign-up", "loginCheck", // USER API
    "/productdetail/**",  //PRODUCT VIEW
    "/api/product/**", // PRODUCT API
    "/cart/view" //CART VIEW
];

// 패턴을 정규 표현식으로 변환하는 함수
function patternToRegex(pattern) {
    const regexStr = pattern
        .replace(/\*\*/g, '.*') // '**'를 '.*'로 변환
        .replace(/\*/g, '[^/]*') // '*'를 '[^/]*'로 변환
        .replace(/{[^}]+}/g, '[^/]+') // '{variable}'를 '[^/]+'로 변환
        .replace(/\//g, '\\/'); // '/'를 '\/'로 변환
    return new RegExp(`^${regexStr}$`);
}

// 패턴 매칭 함수
function matchesAnyPattern(url, patterns) {
    return patterns.some(pattern => patternToRegex(pattern).test(url));
}

// 기존 fetch를 래핑하여 인터셉터를 추가
const originalFetch = window.fetch;

window.fetch = async (url, options = {}) => {
    // 예외 URL인지 확인
    const isTokenNotRequired = matchesAnyPattern(url, WHITER_LIST);

    if (!isTokenNotRequired) {
        const response = await originalFetch(url, options);

        if (response.status === 401) {
            // 토큰 만료로 401 응답이 올 경우 토큰 재발급 요청
            const reissueResponse = await fetch('/reissue', {
                method: 'POST',
                credentials: 'include'
            });

            if (reissueResponse.ok) {
                // 재발급 성공 시 원래 요청을 다시 보냄
                const newResponse = await originalFetch(url, options);
                return newResponse;
            } else {
                // 재발급 실패 시 에러 처리
                console.error('Failed to reissue token');
                return response;
            }
        }

        return response;
    } else {
        return originalFetch(url, options);
    }
};
