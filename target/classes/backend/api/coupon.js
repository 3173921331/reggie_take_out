// 查询列表接口
function getCouponPage(params) {
  return $axios({
    url: `/coupon/page`,
    method: `get`,
    params
  })
}

// 新增优惠券
function addCoupon(params) {
  return $axios({
    url: `/coupon`,
    method: `post`,
    data: { ...params }
  })
}

// 修改优惠券
function editCoupon(params) {
  return $axios({
    url: `/coupon`,
    method: `put`,
    data: { ...params }
  })
}

// 删除优惠券
function deleteCoupon(id) {
  return $axios({
    url: `/coupon?ids=` + id,
    method: `delete`,
  })
}

// 根据ID查询详情
function queryCouponById(id) {
  return $axios({
    url: `/coupon/${id}`,
    method: `get`
  })
}


// 修改优惠券状态 (新增)
function couponStatus(params) {
  return $axios({
    url: `/coupon/status/${params.status}`,
    method: `post`,
    params: { ids: params.id }
  })
}

function distributeCouponApi(params) {
  return $axios({ //
    url: `/coupon/distribute`,
    method: `post`,
    params // ?couponId=xxx
  })
}