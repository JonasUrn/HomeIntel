import React from 'react';

const LoadingSpinner = ({ size = 'medium', color = 'rgb(53,109,90)' }) => {
    const sizeMap = {
        small: { width: '30px', height: '30px', border: '3px' },
        medium: { width: '50px', height: '50px', border: '5px' },
        large: { width: '70px', height: '70px', border: '6px' }
    };

    const { width, height, border } = sizeMap[size] || sizeMap.medium;

    const spinnerStyle = {
        width,
        height,
        border: `${border} solid rgba(0, 0, 0, 0.1)`,
        borderTop: `${border} solid ${color}`,
        borderRadius: '50%',
        animation: 'spin 1s linear infinite',
        margin: '20px auto'
    };

    const containerStyle = {
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        padding: '20px'
    };

    return (
        <div style={containerStyle}>
            <style>
                {`
          @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
          }
        `}
            </style>
            <div style={spinnerStyle}></div>
            <p style={{ marginTop: '10px', color: '#666', textAlign: 'center' }}>Loading...</p>
        </div>
    );
};

export default LoadingSpinner;