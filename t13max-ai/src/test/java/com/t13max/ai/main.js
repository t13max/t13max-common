/** 初始化画布 */
const canvas = document.getElementById("canvas");
const ctx = canvas.getContext("2d");
const width = 400;
const height = 280;
ctx.lineJoin = "round";
ctx.lineCap = "round";
canvas.style.width = width * 3 + "px";
canvas.style.height = height * 3  + "px";
const scale = 10;
canvas.width = width * scale;
canvas.height = height * scale;
ctx.scale(scale, scale);
const padding = 2;
const totalWidth = parseInt(canvas.style.width);
const totalHeight = parseInt(canvas.style.height);

/** 点集 */
// 工具函数，生成八边形障碍
const generateObstacle = (x, y, radius) =>
  new Array(8).fill(null).map((_, index) => {
    const angle = (Math.PI / 4) * index; // 45度
    const pointX = x + radius * Math.cos(angle);
    const pointY = y + ((radius * Math.sin(angle)) / height) * width; // 因为getRenderPosition会修改渲染比例
    return { x : pointX, y : pointY };
  });

/** 德劳内三角剖分 */
const generateTriangles = (points) => {
  const triangles = Delaunator.from(points.map((p) => [p.x, p.y])).triangles;
  return triangles.reduce((acc, cur, i) => {
    if (i % 3 !== 0) return acc;
    return [...acc, [triangles[i], triangles[i + 1], triangles[i + 2]].map((index) => points[index])];
  }, []);
};

/** A星算法 */
// 工具函数，检查某个三角网格是否属于某一个障碍
const checkIsObstacle = (triangle, obstacles) =>
  obstacles.some((obstacle) => triangle.every((p1) => obstacle.some((p2) => p1.x === p2.x && p1.y === p2.y)));
// 工具函数，计算三角网格的重心
const getNodeMid = (node) => {
  let x = (node.triangle[0].x + node.triangle[1].x + node.triangle[2].x) / 3;
  let y = (node.triangle[0].y + node.triangle[1].y + node.triangle[2].y) / 3;
  return { x, y };
};
// 工具函数，获取某个node的三条边
const getEdges = (node) => {
  const triangle = node.triangle;
  return [
    [triangle[0], triangle[1]],
    [triangle[1], triangle[2]],
    [triangle[2], triangle[0]],
  ];
};
// 工具函数，判断两条边是否相同
const isSameEdge = (edge1, edge2) => {
  const [p1, p2] = edge1;
  const [q1, q2] = edge2;
  return (
    (p1.x === q1.x && p1.y === q1.y && p2.x === q2.x && p2.y === q2.y) ||
    (p1.x === q2.x && p1.y === q2.y && p2.x === q1.x && p2.y === q1.y)
  );
};
// 工具函数，获取某个网格的三个邻居网格（通过判断是否跟某个三角形有公共边）
const getNeighborNodes = (nodes, curNode) =>
  nodes.filter((otherNode) => {
    if (otherNode === curNode || otherNode.isObstacle) return false;
    for (const curEdge of getEdges(curNode)) {
      for (const otherEdge of getEdges(otherNode)) {
        if (isSameEdge(curEdge, otherEdge)) return true;
      }
    }
    return false;
  });
// 工具函数-计算方向（使用叉乘）
const crossProduct = (a, b, c) => (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
// 工具函数-计算夹角（使用点乘）
const dotProduct = (a, b, c) => {
  const ab = { x : b.x - a.x, y : b.y - a.y };
  const ac = { x : c.x - a.x, y : c.y - a.y };
  const dotProduct = ab.x * ac.x + ab.y * ac.y;
  const lenAB = Math.sqrt(ab.x ** 2 + ab.y ** 2);
  const lenAC = Math.sqrt(ac.x ** 2 + ac.y ** 2);
  const cosTheta = dotProduct / (lenAB * lenAC);
  const angleRadians = Math.acos(cosTheta);
  const angleDegrees = angleRadians * (180 / Math.PI);
  return angleDegrees;
};
// 工具函数，判断点是否在三角形内
const isPointInNode = (node, point) => {
  let [p1, p2, p3] = node.triangle;
  let d1 = crossProduct(p1, p2, point);
  let d2 = crossProduct(p2, p3, point);
  let d3 = crossProduct(p3, p1, point);
  return (d1 >= 0 && d2 >= 0 && d3 >= 0) || (d1 <= 0 && d2 <= 0 && d3 <= 0);
};
// 工具函数，判断point所在node
const getNodeByPoint = (nodes, point) => nodes.find((node) => isPointInNode(node, point));
// 工具函数，计算两个node的公共边
const getCommonEdge = (node1, node2) => {
  for (const curEdge of getEdges(node1)) {
    for (const nextEdge of getEdges(node2)) {
      if (isSameEdge(curEdge, nextEdge)) {
        return curEdge;
      }
    }
  }
};
// 工具函数，获取边的中点
const getEdgeMid = (edge) => {
  const [p1, p2] = edge;
  return { x : (p1.x + p2.x) / 2, y : (p1.y + p2.y) / 2 };
};
// 工具函数，两条边的中点距离
const getEdgeDistance = (edge1, edge2) => {
  const p1 = getEdgeMid(edge1);
  const p2 = getEdgeMid(edge2);
  const distX = Math.abs(p1.x - p2.x);
  const distY = Math.abs(p1.y - p2.y);
  return Math.sqrt(distX ** 2 + distY ** 2);
};
// 工具函数，启发式函数
const heuristic = (edge1, edge2) => getEdgeDistance(edge1, edge2);

// a星寻路
const aStar = ({ startNode, endNode, nodes, startPoint, endPoint }) => {
  startNode.g = 0;
  startNode.h = 0; // 起点随便设置，用不到
  startNode.f = startNode.g + startNode.h;
  const openList = [startNode];
  const closeList = [];
  while (openList.length) {
    // 择优
    openList.sort((a, b) => a.f - b.f);
    const currentNode = openList.shift();
    if (currentNode === endNode) {
      // 到达终点，回溯路径
      const nodePath = [];
      let node = currentNode;
      while (node) {
        nodePath.unshift(node);
        node = node.parent;
      }
      return nodePath;
    }
    // 扩展
    closeList.push(currentNode);
    const neighborNodes = getNeighborNodes(nodes, currentNode);
    for (let neighborNode of neighborNodes) {
      if (closeList.includes(neighborNode)) continue;
      const curCommonEdge = getCommonEdge(currentNode, neighborNode);
      const preCommonEdge = currentNode.parent
        ? getCommonEdge(currentNode, currentNode.parent)
        : new Array(2).fill(startPoint);
      const tempG = currentNode.g + heuristic(curCommonEdge, preCommonEdge);
      if (tempG < neighborNode.g) {
        neighborNode.parent = currentNode;
        neighborNode.g = tempG;
        neighborNode.h = heuristic(curCommonEdge, new Array(2).fill(endPoint));
        neighborNode.f = neighborNode.g + neighborNode.h;
        if (!openList.includes(neighborNode)) openList.push(neighborNode);
      }
    }
  }
};

/** 漏斗算法 */
// 工具函数，两个点是否相同
const isSamePoint = (p1, p2) => p1.x === p2.x && p1.y === p2.y;
// 工具函数，获取边上的另一个点
const getOtherPoint = (edge, p1) => {
  const index = edge.findIndex((p2) => isSamePoint(p1, p2));
  return edge[index === 0 ? 1 : 0];
};
// 漏斗平滑
const funnel = ({ startPoint, endPoint, commonEdge, leftPoints, rightPoints }) => {
  // 漏斗边开始遍历起点
  let nextLeftIndex = 0;
  let nextRightIndex = 0;
  const smoothedPath = [startPoint];
  while (!isSamePoint(smoothedPath[smoothedPath.length - 1], endPoint)) {
    // 漏斗中点
    const mid = smoothedPath[smoothedPath.length - 1];
    let preAngle = 360;
    let preSign = NaN;
    let leftIndex = nextLeftIndex;
    let rightIndex = nextRightIndex;
    const leftTotal = leftPoints.length - 1;
    const rightTotal = rightPoints.length - 1;
    let leftMoved = false;
    const lefts = leftPoints.map((point) => ({ point, disable : false }));
    const rights = rightPoints.map((point) => ({ point, disable : false }));
    // 前进直到非disabled节点
    const addLeftIndex = () => {
      if (leftIndex >= leftTotal) return false;
      const start = leftIndex + 1;
      const index = lefts.slice(start).findIndex(e => !e.disable);
      if (index === -1) return false;
      leftIndex = start + index;
      return true;
    };
    // 后退直到非disabled节点
    const reduceLeftIndex = () => {
      leftIndex = (() => {
        let tempIndex = leftIndex;
        while (tempIndex > 0 && lefts[tempIndex].disable) tempIndex--;
        return tempIndex;
      })();
    };
    const addRightIndex = () => {
      if (rightIndex >= rightTotal) return false;
      const start = rightIndex + 1;
      const index = rights.slice(start).findIndex(e => !e.disable);
      if (index === -1) return false;
      rightIndex = start + index;
      return true;

    };
    const reduceRightIndex = () => {
      rightIndex = (() => {
        let tempIndex = rightIndex;
        while (tempIndex > 0 && rights[tempIndex].disable) tempIndex--;
        return tempIndex;
      })();
    };
    const addIndex = (_leftIndex = leftIndex, _rightIndex = rightIndex) => {
      const leftIndexAtCommonEdge = commonEdge.findLastIndex((edge) =>
        edge.some((p) => isSamePoint(p, lefts[_leftIndex].point))
      );
      const rightIndexAtCommonEdge = commonEdge.findLastIndex((edge) =>
        edge.some((p) => isSamePoint(p, rights[_rightIndex].point))
      );
      // 比较左右index在公共边上的位置，靠前的优先移动
      if (leftIndexAtCommonEdge < rightIndexAtCommonEdge) {
        const success = addLeftIndex();
        leftMoved = success;
        !success && addRightIndex();
      } else {
        const success = addRightIndex();
        leftMoved = !success;
        if (!success) addLeftIndex();
      }
    };
    while (true) {
      const left = lefts[leftIndex].point;
      const right = rights[rightIndex].point;
      const angle = dotProduct(mid, left, right);
      const sign = crossProduct(mid, left, right);
      // debugger

      // 1.符号相反，代表两条漏斗边发生跨越
      // 2.叉积为0，代表两条漏斗边平行，到点终点或者跟终点平行
      if ((preSign > 0 && sign < 0) || (preSign < 0 && sign > 0) || sign === 0) {
        const target = leftMoved ? right : left;
        // 保存路径点
        smoothedPath.push(target);
        const edge = commonEdge.findLast((edge) => edge.some((p) => isSamePoint(p, target))); // 拐点所在最后的公共边
        const p1 = getOtherPoint(edge, target);
        // 决定下次迭代的两个点
        if (leftMoved) {
          nextLeftIndex = lefts.findIndex(({ point : p2 }) => isSamePoint(p1, p2));
          nextRightIndex = rightIndex + 1;
        } else {
          nextRightIndex = rights.findIndex(({ point : p2 }) => isSamePoint(p1, p2));
          nextLeftIndex = leftIndex + 1;
        }
        break;
      }

      if (angle <= preAngle) {
        (leftMoved ? lefts : rights).map((v) => (v.disable = false));
        addIndex();
        preAngle = angle; // 记录temp
        preSign = sign;
      } else {
        if (leftMoved) {
          lefts[leftIndex].disable = true;
          let tempIndex = leftIndex;
          reduceLeftIndex();
          addIndex(tempIndex, rightIndex);
        } else {
          rights[rightIndex].disable = true;
          let tempIndex = rightIndex;
          reduceRightIndex();
          addIndex(leftIndex, tempIndex);
        }
      }
    }
  }
  return smoothedPath;
};

// 工具函数，逻辑坐标转成渲染坐标，用padding防止溢出
const getRenderPosition = ({ x, y }) => ({
  x : (x / 100) * (width - padding * 2) + padding,
  y : (y / 100) * (height - padding * 2) + padding,
});
// 工具函数，保留几位
const toFixed = (num, digits) => parseFloat(num.toFixed(digits));
/** 画点 */
const drawPoint = ({points}) => {
  for (const p of points) {
    const point = getRenderPosition({ x : p.x, y : p.y });
    const radius = 2;
    ctx.fillStyle = "#ff6398";
    ctx.beginPath();
    ctx.arc(point.x, point.y, radius, 0, Math.PI * 2);
    ctx.closePath();
    ctx.fill();
  }
  for (const p of [startPoint, endPoint]) {
    const point = getRenderPosition({ x : p.x, y : p.y });
    const radius = 3;
    ctx.fillStyle = "#ff6398";
    ctx.beginPath();
    ctx.arc(point.x, point.y, radius, 0, Math.PI * 2);
    ctx.closePath();
    ctx.fill();
  }
};
/** 画线 */
const drawEdge = ({ nodes,nodePath }) => {
  for (const node of nodes) {
    const [a, b, c] = node.triangle;
    const p1 = getRenderPosition({ x : a.x, y : a.y });
    const p2 = getRenderPosition({ x : b.x, y : b.y });
    const p3 = getRenderPosition({ x : c.x, y : c.y });
    // 图形
    ctx.beginPath();
    ctx.moveTo(p1.x, p1.y);
    ctx.lineTo(p2.x, p2.y);
    ctx.lineTo(p3.x, p3.y);
    ctx.closePath();
    // 填充
    ctx.fillStyle =(()=>{
      if(node.isObstacle){
        return "#404040"
      }else if(nodePath.some(n=>n === node)){
        return "rgba(255,99,152,0.5)"
      }else{
        return "transparent";
      }
    })();
    ctx.fill();
    // 边框
    ctx.lineWidth = 0.75;
    ctx.strokeStyle = "#65ddfd";
    ctx.stroke();
    // 索引文字
    // ctx.font = "5px SimHei";
    // ctx.fillStyle = "#888";
    // ctx.textAlign = "center";
    // ctx.textBaseline = "middle";
    // const mid = getNodeMid(node);
    // const textPos = getRenderPosition({ x : mid.x, y : mid.y });
    // ctx.fillText(`${ node.index }`, textPos.x, textPos.y);
  }
};
/** 画公共线 */
const drawCommonEdge = ({ commonEdge }) => {
  for (let i = 0; i < commonEdge.length; i++) {
    for (const edge of commonEdge) {
      const [p1, p2] = edge;
      const cur = getRenderPosition({ x : p1.x, y : p1.y });
      const next = getRenderPosition({ x : p2.x, y : p2.y });
      // 画图形
      ctx.beginPath();
      ctx.moveTo(cur.x, cur.y);
      ctx.lineTo(next.x, next.y);
      ctx.closePath();
      // 边框
      ctx.lineWidth = 1;
      ctx.strokeStyle = "#feb94a";
      ctx.stroke();
    }
  }
};
/** 画A星网格路径 */
const drawNodePath = ({ nodePath }) => {
  const path = [startPoint];
  for (let i = 0; i < nodePath.length - 1; i++) {
    path.push(getEdgeMid(getCommonEdge(nodePath[i], nodePath[i + 1])));
  }
  path.push(endPoint);
  for (let i = 0; i < path.length - 1; i++) {
    const p1 = path[i];
    const p2 = path[i + 1];
    const cur = getRenderPosition({ x : p1.x, y : p1.y });
    const next = getRenderPosition({ x : p2.x, y : p2.y });
    ctx.beginPath();
    ctx.moveTo(cur.x, cur.y);
    ctx.lineTo(next.x, next.y);
    ctx.closePath();
    ctx.lineWidth = 1;
    ctx.strokeStyle = "#ee938f";
    ctx.stroke();
  }
};
/** 画漏斗平滑路径 */
const drawSmoothedPath = ({ smoothedPath }) => {
  for (let i = 0; i < smoothedPath.length - 1; i++) {
    const p1 = smoothedPath[i];
    const p2 = smoothedPath[i + 1];
    const cur = getRenderPosition({ x : p1.x, y : p1.y });
    const next = getRenderPosition({ x : p2.x, y : p2.y });
    ctx.beginPath();
    ctx.moveTo(cur.x, cur.y);
    ctx.lineTo(next.x, next.y);
    ctx.closePath();
    ctx.lineWidth = 1.25;
    ctx.strokeStyle = "#aae062";
    ctx.stroke();
  }
};
/** 寻路核心流程 */
const main = ({ startPoint, endPoint, canvas }) => {
  // 地图边界
  const map = [
    { x : 0, y : 0 },
    { x : 0, y : 100 },
    { x : 100, y : 0 },
    { x : 100, y : 100 },
  ];
  // 障碍
  const obstacles = (() => {
    const obstacles = [
      // generateObstacle(30, 46, 10),
      // generateObstacle(55, 40, 10),
      generateObstacle(40, 25, 10),
      generateObstacle(80, 21, 15),
      generateObstacle(15, 14, 10),
      generateObstacle(25, 75, 14),
      generateObstacle(60, 58, 16),
      generateObstacle(85, 80, 10),
      generateObstacle(20, 45, 6),
      generateObstacle(90, 52, 8),
      generateObstacle(50, 89, 8),
    ];
    return obstacles.map((ob) =>
      ob.map((p) => {
        return { x : toFixed(p.x, 8), y : toFixed(p.y, 8) };
      })
    );
  })();
  // 点集
  const points = [...map, ...obstacles.flat()];
  // console.log("points",points)
  // 三角剖分
  const triangles = generateTriangles(points);
  // 三角网格封装成node
  const nodes = triangles.map((triangle, index) => ({
    index, // 方便标识
    triangle, // 对应的三角网格
    isObstacle : checkIsObstacle(triangle, obstacles), // 是否是障碍node
    g : Infinity, // 到该节点的代价
    h : 0, // 到达目标点点代价
    f : 0, // 启发式评估值
    parent : null, // 父节点，用于回溯
  }));
  // 防止点击障碍
  if (getNodeByPoint(nodes, startPoint)?.isObstacle || getNodeByPoint(nodes, endPoint)?.isObstacle) return;

  // 网格路径
  const startNode = getNodeByPoint(nodes, startPoint);
  const endNode = getNodeByPoint(nodes, endPoint);
  // a星网格寻路
  const nodePath = aStar({ startNode, endNode, nodes, startPoint, endPoint });
  // console.log("nodePath", nodePath);
  // 网格路径公共边
  const commonEdge = (() => {
    const result = [];
    for (let i = 0; i < nodePath.length - 1; i++) {
      result.push(getCommonEdge(nodePath[i], nodePath[i + 1]));
    }
    return [...result, [endPoint, endPoint]]; // 最后一项特殊处理，把终点看作一条端点相同的边
  })();
  // console.log("commonEdge",commonEdge)
  // 网格路径区分左右两条路径点
  const [leftPoints, rightPoints] = (() => {
    const leftPoints = [commonEdge[0][1]]; // 随便放就行，只要能把端点分成两组
    const rightPoints = [];
    for (let i = 0; i < commonEdge.length - 1; i++) {
      const [one, two] = commonEdge[i];
      const oneInLeft = leftPoints.some((point) => isSamePoint(point, one));
      const oneInRight = rightPoints.some((point) => isSamePoint(point, one));
      const twoInLeft = leftPoints.some((point) => isSamePoint(point, two));
      const twoInRight = rightPoints.some((point) => isSamePoint(point, two));
      if (oneInLeft) {
        rightPoints.push(two);
      } else if (oneInRight) {
        leftPoints.push(two);
      } else if (twoInLeft) {
        rightPoints.push(one);
      } else if (twoInRight) {
        leftPoints.push(one);
      }
    }
    leftPoints.push(endPoint);
    rightPoints.push(endPoint);
    return [leftPoints, rightPoints];
  })();
  // console.log("leftPoints", leftPoints)
  // console.log("rightPoints", rightPoints)
  // 漏斗优化路径
  const smoothedPath = funnel({
    startPoint,
    endPoint,
    commonEdge,
    leftPoints,
    rightPoints,
  });
  // console.log("smoothedPath", smoothedPath);

  // 渲染
  ctx.clearRect(0, 0, width, height);
  drawEdge({ nodes,nodePath });
  drawCommonEdge({ commonEdge });
  drawNodePath({ nodePath });
  drawSmoothedPath({ smoothedPath });
  drawPoint({ points });
};

let startPoint = { x : 5, y : 5 };
let endPoint = { x : 75, y : 50 };

/** 交互 */
// 点击设置起点
canvas.addEventListener("click", (e) => {
  const rect = canvas.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top - padding;
  if (!(x < totalWidth && x > 0 && y < totalHeight && y > 0)) return false;
  startPoint = { x : (x / totalWidth) * 100, y : (y / totalHeight) * 100 };
  return false;
});
// 滑动设置终点
canvas.addEventListener("mousemove", (e) => {
  e.stopPropagation();
  e.preventDefault();
  const rect = canvas.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top - padding;
  if (!(x < totalWidth && x > 0 && y < totalHeight && y > 0)) return false;
  endPoint = { x : (x / totalWidth) * 100, y : (y / totalHeight) * 100 };
  return false;
});

/** 循环 */
const tick = () => {
  main({ startPoint, endPoint, canvas });
  requestAnimationFrame(tick);
};

tick();
